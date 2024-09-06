package com.example.bmi_caculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtHeight, edtWeight;
    private CheckBox cbSaveInfo;
    private TextView tvResult;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "BMIPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các view
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);
        cbSaveInfo = findViewById(R.id.cbSaveInfo);
        tvResult = findViewById(R.id.tvResult);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        Button btnReset = findViewById(R.id.btnReset);

        // Lấy SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadSavedData();

        cbSaveInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Lưu thông tin khi checkbox được chọn
                    saveInfoToPreferences();
                }
            }
        });

        // Sự kiện khi nhấn nút Calculate
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateBMI();
            }
        });

        // Sự kiện khi nhấn nút Reset
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Lưu chiều cao, cân nặng và kết quả hiện tại vào Bundle
        outState.putString("height", edtHeight.getText().toString());
        outState.putString("weight", edtWeight.getText().toString());
        outState.putString("result", tvResult.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Khôi phục chiều cao, cân nặng và kết quả từ Bundle
        if (savedInstanceState != null) {
            edtHeight.setText(savedInstanceState.getString("height"));
            edtWeight.setText(savedInstanceState.getString("weight"));
            tvResult.setText(savedInstanceState.getString("result"));
        }
    }


    // Hàm tính toán BMI
    private void calculateBMI() {
        String heightStr = edtHeight.getText().toString().trim();
        String weightStr = edtWeight.getText().toString().trim();

        // Kiểm tra xem người dùng đã nhập đúng dữ liệu hay chưa
        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            try {
                float heightCm = Float.parseFloat(heightStr); // Đơn vị cm
                float weight = Float.parseFloat(weightStr);   // Đơn vị kg

                // Kiểm tra chiều cao và cân nặng có hợp lệ hay không
                if (heightCm <= 0 || weight <= 0) {
                    tvResult.setText("Chiều cao và cân nặng phải lớn hơn 0.");
                    return;
                }

                // Chuyển đổi chiều cao từ cm sang mét
                float height = heightCm / 100;

                // Tính chỉ số BMI
                float bmi = weight / (height * height);

                String bmiCategory;
                if (bmi < 16) {
                    bmiCategory = "Rất gầy (BMI < 16)";
                } else if (bmi >= 16 && bmi < 17) {
                    bmiCategory = "Tương đối gầy (BMI từ 16 - 17)";
                } else if (bmi >= 17 && bmi < 18.5) {
                    bmiCategory = "Hơi gầy (BMI từ 17 - 18.5)";
                } else if (bmi >= 18.5 && bmi < 25) {
                    bmiCategory = "Bình thường (BMI từ 18.5 - 25)";
                } else if (bmi >= 25 && bmi < 30) {
                    bmiCategory = "Hơi béo (BMI từ 25 - 30)";
                } else if (bmi >= 30 && bmi < 35) {
                    bmiCategory = "Béo phì loại I (BMI từ 30 - 35)";
                } else if (bmi >= 35 && bmi < 40) {
                    bmiCategory = "Béo phì loại II (BMI từ 35 - 40)";
                } else {
                    bmiCategory = "Béo phì loại III (BMI > 40)";
                }

                tvResult.setText("Chỉ số BMI của bạn là: " + String.format("%.2f", bmi) + "\nBạn thuộc nhóm: " + bmiCategory);

                // Lưu thông tin nếu chọn "Lưu thông tin"
                if (cbSaveInfo.isChecked()) {
                    saveInfoToPreferences();
                }
            } catch (NumberFormatException e) {
                tvResult.setText("Vui lòng nhập đúng định dạng số.");
            }
        } else {
            tvResult.setText("Vui lòng nhập đầy đủ thông tin!");
        }
    }

    // Hàm lưu thông tin vào SharedPreferences
    private void saveInfoToPreferences() {
        String heightStr = edtHeight.getText().toString().trim();
        String weightStr = edtWeight.getText().toString().trim();
        String resultStr = tvResult.getText().toString().trim();

        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("height", heightStr);
            editor.putString("weight", weightStr);
            editor.putString("result", resultStr);
            editor.apply();
            tvResult.setText("Thông tin đã được lưu.");
        } else {
            tvResult.setText("Vui lòng nhập đầy đủ thông tin trước khi lưu.");
        }
    }


    // Hàm reset các trường dữ liệu
    private void resetFields() {
        edtHeight.setText("");
        edtWeight.setText("");
        tvResult.setText("Kết quả BMI");
        cbSaveInfo.setChecked(false);

        // Xóa thông tin đã lưu trong SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Hàm tải dữ liệu đã lưu
    private void loadSavedData() {
        String savedHeight = sharedPreferences.getString("height", "");
        String savedWeight = sharedPreferences.getString("weight", "");
        String savedResult = sharedPreferences.getString("result", "");

        if (!savedHeight.isEmpty() && !savedWeight.isEmpty()) {
            edtHeight.setText(savedHeight);
            edtWeight.setText(savedWeight);
            tvResult.setText(savedResult);
            cbSaveInfo.setChecked(true); // Đánh dấu checkbox nếu dữ liệu đã được lưu
        }
    }


}

