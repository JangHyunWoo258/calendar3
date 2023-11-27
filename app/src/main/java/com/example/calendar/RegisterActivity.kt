package com.example.calendar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calendar.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordConfirmEditText: EditText
    private lateinit var joinButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Firebase Authentication 초기화
        firebaseAuth = FirebaseAuth.getInstance()

        // UI 요소들과 연결
        nameEditText = findViewById(R.id.join_name)
        emailEditText = findViewById(R.id.join_email)
        passwordEditText = findViewById(R.id.join_password)
        passwordConfirmEditText = findViewById(R.id.join_pwck)
        joinButton = findViewById(R.id.join_button)

        // 회원가입 버튼 클릭 리스너 설정
        joinButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordConfirm = passwordConfirmEditText.text.toString()

            if (areFieldsEmpty(name, email, password, passwordConfirm)) {
                showToast("모든 필드를 입력하세요.")
            } else if (!arePasswordsMatching(password, passwordConfirm)) {
                showToast("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
            } else {
                // Firebase Authentication을 사용하여 회원가입 시도
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // 회원가입 성공
                            showToast("회원가입 성공")
                            navigateToLoginActivity()
                        } else {
                            // 회원가입 실패
                            showToast("회원가입 실패: ${task.exception?.message}")
                        }
                    }
            }
        }
    }

    private fun areFieldsEmpty(name: String, email: String, password: String, passwordConfirm: String): Boolean {
        return name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()
    }

    private fun arePasswordsMatching(password: String, passwordConfirm: String): Boolean {
        return password == passwordConfirm
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Optional: 현재 활동을 종료하여 뒤로 돌아갈 수 없도록 설정
    }
}