package org.bbqqvv.backendecommerce.util;

import java.util.Locale;


public class SlugUtils {

    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Bước 1: Tiến hành thay thế các ký tự có dấu với ký tự tương ứng không dấu.
        String normalized = input
                // Dấu trong tiếng Việt
                .replaceAll("à|á|ạ|ả|ã|ạ", "a")
                .replaceAll("â|ầ|ấ|ậ|ẩ|ẫ", "a")
                .replaceAll("ă|ằ|ắ|ặ|ẳ|ẵ", "a")
                .replaceAll("è|é|ẹ|ẻ|ẽ|ệ", "e")
                .replaceAll("ê|ề|ế|ệ|ể|ễ", "e")
                .replaceAll("ì|í|ị|ỉ|ĩ", "i")
                .replaceAll("ò|ó|ọ|ỏ|õ|ố|ồ|ộ|ổ|ỗ", "o")
                .replaceAll("ô|ồ|ố|ộ|ổ|ỗ", "o")
                .replaceAll("ơ|ờ|ớ|ợ|ở|ỡ", "o")
                .replaceAll("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ", "u")
                .replaceAll("ý|ỳ|ỵ|ỷ|ỹ", "y")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D")

                // Thêm các ký tự có dấu tiếng Việt khác
                .replaceAll("ò|ó|ọ|ỏ|õ", "o")
                .replaceAll("ú|ù|ũ|ủ", "u")
                .replaceAll("é|è|ẹ|ẻ|ẽ", "e")
                .replaceAll("í|ì|ỉ|ĩ|ị", "i")
                .replaceAll("ô|ồ|ố|ộ|ổ", "o")
                .replaceAll("ề|ế|ệ|ể|ễ", "e")
                .replaceAll("ả|ạ|á|à|ã", "a")
                .replaceAll("ừ|ứ|ự|ử|ữ", "u")

                // Thêm ký tự đặc biệt tiếng Việt và Unicode khác
                .replaceAll("ç", "c")   // ký tự ç
                .replaceAll("ñ", "n")   // ký tự ñ
                .replaceAll("ã", "a")   // ký tự ã
                .replaceAll("é", "e")   // ký tự é
                .replaceAll("ê", "e")   // ký tự ê
                .replaceAll("å", "a")   // ký tự å
                .replaceAll("ø", "o")   // ký tự ø
                .replaceAll("ý", "y")   // ký tự ý

                // Bước 2: Chuyển thành chữ thường
                .toLowerCase(Locale.ENGLISH);

        // Bước 3: Loại bỏ các ký tự không hợp lệ, chỉ giữ lại chữ cái, số, khoảng trắng, và dấu gạch ngang
        normalized = normalized.replaceAll("[^a-z0-9\\s-]", "");

        // Bước 4: Thay khoảng trắng thành dấu gạch ngang
        normalized = normalized.replaceAll("\\s+", "-"); // Đổi khoảng trắng thành dấu gạch ngang

        // Bước 5: Loại bỏ dấu gạch ngang thừa (nếu có nhiều dấu gạch ngang liên tiếp)
        normalized = normalized.replaceAll("-+", "-"); // Gộp các dấu gạch ngang liên tiếp thành 1

        // Bước 6: Loại bỏ dấu gạch ngang ở đầu và cuối
        normalized = normalized.replaceAll("^-|-$", ""); // Loại bỏ dấu gạch ngang ở đầu hoặc cuối

        return normalized;
    }

    public static void main(String[] args) {
        String input = "Tây âu";
        String slug = toSlug(input);
        System.out.println(slug); // In kết quả slug
    }
}
