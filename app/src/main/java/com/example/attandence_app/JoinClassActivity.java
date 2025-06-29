package com.example.attandence_app;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class JoinClassActivity extends AppCompatActivity {

    private EditText editClassCode;
    private Button btnJoinClass,btnExit;
    private TextView txtJoinResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        editClassCode = findViewById(R.id.editClassCode);
        btnJoinClass = findViewById(R.id.btnJoinClass);
        btnExit=findViewById(R.id.btnExit);
        txtJoinResult = findViewById(R.id.txtJoinResult);


        btnExit.setOnClickListener(view -> finishAffinity());

        btnJoinClass.setOnClickListener(view -> joinClass());
    }

    private void joinClass() {
        String classCode = editClassCode.getText().toString().trim();

        if (classCode.isEmpty()) {
            Toast.makeText(this, "Please enter a class code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulated class code validation (Replace with actual database check)
        if (classCode.equals("ABC123")) { // Example valid code
            txtJoinResult.setText("Successfully joined class!");
            txtJoinResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            txtJoinResult.setText("Invalid class code, try again!");
            txtJoinResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        txtJoinResult.setVisibility(View.VISIBLE); // Show result message
    }
}
