package com.kewwi.quickmem.data.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.kewwi.quickmem.data.QMDatabaseHelper;
import com.kewwi.quickmem.data.model.Card;

import java.util.ArrayList;

public class CardDAO {
    // Khởi tạo database helper và đối tượng SQLiteDatabase
    QMDatabaseHelper qmDatabaseHelper;
    SQLiteDatabase sqLiteDatabase;

    // Hàm khởi tạo CardDAO với context của ứng dụng
    public CardDAO(Context context) {
        qmDatabaseHelper = new QMDatabaseHelper(context);
    }

    // Hàm insertCard: Thêm một thẻ mới vào database
    public long insertCard(Card card) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        long result = 0;

        // Chuẩn bị dữ liệu cho bảng
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", card.getId());
        contentValues.put("front", card.getFront());
        contentValues.put("back", card.getBack());
        contentValues.put("status", card.getStatus());
        contentValues.put("is_learned", card.getIsLearned());
        contentValues.put("flashcard_id", card.getFlashcard_id());
        contentValues.put("created_at", card.getCreated_at());
        contentValues.put("updated_at", card.getUpdated_at());

        // Thêm thẻ vào bảng và trả về kết quả
        try {
            result = sqLiteDatabase.insert(QMDatabaseHelper.TABLE_CARDS, null, contentValues);
        } catch (SQLException e) {
            Log.e("CardDAO", "insertCard: " + e);
        }
        return result;
    }

    // Hàm countCardByFlashCardId: Đếm số lượng thẻ theo flashcard_id
    public int countCardByFlashCardId(String flashcard_id) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        String query = "SELECT * FROM " + QMDatabaseHelper.TABLE_CARDS + " WHERE flashcard_id = '" + flashcard_id + "'";
        int count = 0;

        // Thực hiện truy vấn và đếm số lượng thẻ
        try {
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            count = cursor.getCount();
        } catch (SQLException e) {
            Log.e("CardDAO", "countCardByFlashCardId: " + e);
        } finally {
            sqLiteDatabase.close();
        }
        return count;
    }

    // Hàm getCardsByFlashCardId: Lấy danh sách các thẻ theo flashcard_id
    public ArrayList<Card> getCardsByFlashCardId(String flashcard_id) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        ArrayList<Card> cards = new ArrayList<>();
        String query = "SELECT * FROM " + QMDatabaseHelper.TABLE_CARDS + " WHERE flashcard_id = '" + flashcard_id + "'";

        // Thực hiện truy vấn và trả về danh sách thẻ
        try {
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            cards = getCardsFromCursor(cursor);
        } catch (SQLException e) {
            Log.e("CardDAO", "getCardsByFlashCardId: " + e);
        }
        return cards;
    }

    // Hàm getAllCardByStatus: Lấy danh sách các thẻ có status 0 hoặc 2 theo flashcard_id
    public ArrayList<Card> getAllCardByStatus(String flashcard_id) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        ArrayList<Card> cards = new ArrayList<>();
        String query = "SELECT * FROM " + QMDatabaseHelper.TABLE_CARDS + " WHERE flashcard_id = '" + flashcard_id + "' AND status != 1";

        // Thực hiện truy vấn và trả về danh sách thẻ
        try {
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            cards = getCardsFromCursor(cursor);
        } catch (SQLException e) {
            Log.e("CardDAO", "getAllCardByStatus: " + e);
        }
        return cards;
    }

    // Hàm deleteCardById: Xóa thẻ theo id
    public long deleteCardById(String id) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        long result = 0;

        // Thực hiện lệnh xóa và trả về kết quả
        try {
            result = sqLiteDatabase.delete(QMDatabaseHelper.TABLE_CARDS, "id = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("CardDAO", "deleteCardById: " + e);
        }
        return result;
    }

    // Hàm updateCardStatusById: Cập nhật trạng thái của thẻ theo id
    public long updateCardStatusById(String id, int status) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        long result = 0;

        // Chuẩn bị giá trị cần cập nhật và thực hiện cập nhật
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);

        try {
            result = sqLiteDatabase.update(QMDatabaseHelper.TABLE_CARDS, contentValues, "id = ?", new String[]{id});
        } catch (SQLException e) {
            Log.e("CardDAO", "updateCardStatusById: " + e);
        }
        return result;
    }

    // Hàm getCardByIsLearned: Lấy danh sách các thẻ có is_learned = 0 theo flashcard_id
    public ArrayList<Card> getCardByIsLearned(String flashcard_id, int is_learned) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        ArrayList<Card> cards = new ArrayList<>();
        String query = "SELECT * FROM " + QMDatabaseHelper.TABLE_CARDS + " WHERE flashcard_id = '" + flashcard_id + "' AND is_learned = " + is_learned;

        // Thực hiện truy vấn và trả về danh sách thẻ
        try {
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            cards = getCardsFromCursor(cursor);
        } catch (SQLException e) {
            Log.e("CardDAO", "getCardByIsLearned: " + e);
        }
        return cards;
    }

    // Hàm checkCardExist: Kiểm tra xem thẻ có tồn tại trong bảng không
    public boolean checkCardExist(String card_id) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        String query = "SELECT * FROM " + QMDatabaseHelper.TABLE_CARDS + " WHERE id = '" + card_id + "'";

        // Thực hiện truy vấn và kiểm tra kết quả
        try {
            @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                return true;
            }
        } catch (SQLException e) {
            Log.e("CardDAO", "checkCardExist: " + e);
        }
        return false;
    }

    // Hàm updateCardById: Cập nhật thông tin của thẻ theo id
    public long updateCardById(Card card) {
        sqLiteDatabase = qmDatabaseHelper.getWritableDatabase();
        long result = 0;

        // Chuẩn bị dữ liệu cần cập nhật và thực hiện lệnh cập nhật
        ContentValues contentValues = new ContentValues();
        contentValues.put("front", card.getFront());
        contentValues.put("back", card.getBack());
        contentValues.put("flashcard_id", card.getFlashcard_id());
        contentValues.put("created_at", card.getCreated_at());
        contentValues.put("updated_at", card.getUpdated_at());

        try {
            result = sqLiteDatabase.update(QMDatabaseHelper.TABLE_CARDS, contentValues, "id = ?", new String[]{card.getId()});
        } catch (SQLException e) {
            Log.e("CardDAO", "updateCardById: " + e);
        }
        return result;
    }

    // Hàm getCardsFromCursor: Chuyển dữ liệu từ con trỏ Cursor thành danh sách các đối tượng Card
    private ArrayList<Card> getCardsFromCursor(Cursor cursor) {
        ArrayList<Card> cards = new ArrayList<>();
        if (sqLiteDatabase.isOpen()) { // Kiểm tra nếu database đang mở
            if (cursor.moveToFirst()) {
                do {
                    Card card = new Card();
                    card.setId(cursor.getString(0));
                    card.setFront(cursor.getString(1));
                    card.setBack(cursor.getString(2));
                    card.setFlashcard_id(cursor.getString(3));
                    card.setStatus(cursor.getInt(4));
                    card.setIsLearned(cursor.getInt(5));
                    card.setCreated_at(cursor.getString(6));
                    card.setUpdated_at(cursor.getString(7));
                    cards.add(card);
                } while (cursor.moveToNext());
            }
        } else {
            Log.d("CardDAO", "Database is closed.");
        }
        return cards;
    }
}
