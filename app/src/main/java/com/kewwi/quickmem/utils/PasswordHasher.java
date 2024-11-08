// Tên file: PasswordHasher.java
// Chức năng chính: PasswordHasher là một lớp cung cấp các phương thức để mã hóa mật khẩu và kiểm tra mật khẩu.
// Lớp này sử dụng thuật toán SHA-256 để băm mật khẩu nhằm đảm bảo an toàn cho dữ liệu người dùng.

package com.kewwi.quickmem.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    // Phương thức để băm mật khẩu
    public static String hashPassword(String password) {
        try {
            // Tạo một instance của MessageDigest sử dụng thuật toán SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Cập nhật các byte của mật khẩu vào digest
            md.update(password.getBytes());

            // Lấy các byte của hash
            byte[] bytes = md.digest();

            // Chuyển đổi các byte này sang định dạng thập lục phân
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            // Trả về mật khẩu đã băm ở định dạng thập lục phân
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Ghi log lỗi nếu thuật toán không tồn tại
            Log.e("PasswordHasher", "Error hashing password", e);
        }
        return null; // Trả về null nếu có lỗi xảy ra
    }

    // Phương thức để kiểm tra mật khẩu
    public static boolean checkPassword(String password, String hashedPassword) {
        // Băm mật khẩu dạng văn bản
        String hashOfInput = hashPassword(password);
        Log.d("PasswordHasher", "checkPassword: " + hashOfInput);

        // So sánh mật khẩu đã băm với mật khẩu đã băm ban đầu
        return hashOfInput != null && hashOfInput.equals(hashedPassword);
    }
}
