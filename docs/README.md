<h1 align="center">ứng dụng thẻ lật</h1>

Thật khó để có thể chọn một đề tài phù hợp cho dự án một. Qua tìm hiểu và nghiên cứu các ứng dụng hiện nay trên cửa hàng ứng dụng, nhóm chúng em thấy có rất nhiều ứng dụng hay như ứng dụng thương mại điện tử, sức khoẻ, tài chính, mạng xã hội, giáo dục. Nhưng đa số nó đều khá khó để thực hiện, đòi hỏi phải bỏ thời gian, tiền bạc và công sức ra. Sau một ngày nghiên cứu và tìm tòi nhóm chúng em quyết định sẽ làm một ứng dụng giúp cho học sinh, giáo viên, người đi làm hoặc bất kỳ ai có thể nhanh chóng ghi nhớ kiến thức chẳng hạn như những câu lý thuyết dài, những từ vựng, cấu trúc ngoại ngữ,.. và đồng thời nhóm em cũng cảm thấy ứng dụng phù hợp với yêu cầu của dự án, có thể phát triển lâu dài. 

# Mục lục

- [mục lục](#mục-lục)
- [Giao diện](#Giao-diện)
- [Backend: java + kolin](#backend-java--kolin)
- [Luồng hoạt động của hệ thống](#luồng-hoạt-động-của-hệ-thống)//
  - [Bảo mật và quản lý dữ liệu](#bảo-mật-và-quản-lý-dữ-liệu)//
# 1. General structure of the project

```
/LIS-Management-System
├── console_app/                 # Console App (Android Studio )
├── backend/                     # Backend (java + Kolin)
├── frontend/                    # Frontend (XML)
├── docs/                        # Tài liệu dự án
└── README.md                    # Thông tin dự án và hướng dẫn sử dụng
```

## 1.1. [Benefits structure](../docs/structures/Benefits.md)

## 1.2. Giao diện

- [Link Forder Structure](../docs/structures/FRONTEND.md)

## 1.3. Backend: java+kolin 

- [Link Forder Structure](../docs/structures/BACKEND.md)

## 1.4. ConsoleApp: Android Studio`

- [Link Forder Structure](../docs/structures/LisConsoleApp.md)

# 2. System Operating Flow Description

- [System Operating Flow Description](../docs/structures/System%20Operating%20Flow%20Description.md)

# 3. Bảo mật và quản lý dữ liệu

- `JWT`: Dùng để xác thực người dùng, bảo mật các API.
- `Bcrypt`: Mã hóa mật khẩu trước khi lưu trữ.
- `Rate limiting và CORS`: Bảo vệ API khỏi tấn công brute-force và điều chỉnh quyền truy cập.
- `HTTPS`: Sử dụng để mã hóa dữ liệu trao đổi giữa client và server.
