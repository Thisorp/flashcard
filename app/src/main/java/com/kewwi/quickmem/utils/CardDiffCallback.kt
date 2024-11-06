package com.kewwi.quickmem.utils

import androidx.recyclerview.widget.DiffUtil
import com.kewwi.quickmem.data.model.Card
//định nghĩa một lớp `CardDiffCallback` dùng để so sánh sự khác nhau giữa hai danh sách `Card`.
// Mục đích chính là tối ưu hóa việc cập nhật giao diện RecyclerView khi dữ liệu thay đổi bằng cách xác định các phần tử đã thay đổi,
// thêm mới hoặc xóa bỏ.

// Lớp `CardDiffCallback` kế thừa từ `DiffUtil.Callback`
// giúp so sánh hai danh sách `Card`.
class CardDiffCallback(
    private val oldList: List<Card>,// Danh sách cũ của các đối tượng `Card`.
    private val newList: List<Card>// Danh sách mới của các đối tượng `Card`.
) : DiffUtil.Callback(
) {
    // Phương thức trả về kích thước của danh sách cũ.
    override fun getOldListSize(): Int {
        return oldList.size
    }

    // Phương thức trả về kích thước của danh sách mới.
    override fun getNewListSize(): Int {
        return newList.size
    }
    // Phương thức kiểm tra xem hai phần tử ở vị trí tương ứng trong danh sách cũ
    // và mới có phải là cùng một đối tượng `Card` không.
    // So sánh dựa trên `id` của đối tượng `Card`.
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    // Phương thức kiểm tra xem nội dung của hai phần tử ở vị trí tương ứng trong danh sách cũ
    // và mới có giống nhau không.
    // So sánh toàn bộ đối tượng `Card`.
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}