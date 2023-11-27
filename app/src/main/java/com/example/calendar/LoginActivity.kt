package com.example.calendar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calendar.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth  //로그인 부분 해쉬처리

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var joinButton: Button
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Authentication 초기화
        firebaseAuth = FirebaseAuth.getInstance()

        // XML 레이아웃 파일에 정의된 UI 요소들을 연결합니다.
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        joinButton = findViewById(R.id.join_button)

        // 로그인 버튼 클릭 리스너를 설정합니다.
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Firebase Authentication을 사용하여 이메일 및 비밀번호로 로그인 시도
            firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task != null && task.isSuccessful) {
                        // 로그인 성공
                        showToast("로그인 완료")

                        // 여기에서 로그인 이후의 화면으로 이동합니다.
                        navigateMainActivity()
                    } else {
                        // 로그인 실패
                        if (task != null) {
                            showToast("실패 이유: ${task.exception?.message}")
                        } else {
                            showToast("로그인 작업이 널입니다.")
                        }
                        showToast("로그인 실패: ${task.exception?.message}")
                    }
                }
        }

       binding.joinButton.setOnClickListener{
           val intent : Intent = Intent(this@LoginActivity,RegisterActivity::class.java)
           startActivity(intent)
       }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("userID", firebaseAuth.currentUser?.uid)
        startActivity(intent)
        finish() // Optional: Finish the current activity so that it can't be navigated back to.
    }


}