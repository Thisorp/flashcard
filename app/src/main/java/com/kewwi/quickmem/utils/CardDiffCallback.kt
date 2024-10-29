// Tên file: CardDiffCallback.kt
// Chức năng chính: CardDiffCallback là một lớp sử dụng để so sánh hai danh sách Card để xác định sự khác biệt giữa chúng.
// Nó giúp RecyclerView cập nhật hiệu quả mà không cần phải tải lại toàn bộ danh sách, chỉ cập nhật những phần đã thay đổi.
package com.kewwi.quickmem.utils

import androidx.recyclerview.widget.DiffUtil
import com.kewwi.quickmem.data.model.Card
// Khai báo lớp CardDiffCallback, thừa kế từ DiffUtil.Callback
class CardDiffCallback(
    private val oldList: List<Card>,// Danh sách cũ
    private val newList: List<Card>// Danh sách mới
) : DiffUtil.Callback(
) {
    // Trả về kích thước của danh sách cũ
    override fun getOldListSize(): Int {
        return oldList.size
    }

    // Trả về kích thước của danh sách mới
    override fun getNewListSize(): Int {
        return newList.size
    }

    // Kiểm tra xem hai mục có phải là cùng một mục hay không
    // So sánh dựa trên id của Card
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    // Kiểm tra xem nội dung của hai mục có giống nhau hay không
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}