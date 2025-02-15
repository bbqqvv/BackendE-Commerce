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
                .replaceAll("[ÀÁẠẢÃAàáạảã]", "a")
                .replaceAll("[ÂẦẤẬẨẪâầấậẩẫ]", "a")
                .replaceAll("[ĂẰẮẶẲẴăằắặẳẵ]", "a")
                .replaceAll("[ÈÉẸẺẼỆèéẹẻẽệ]", "e")
                .replaceAll("[ÊỀẾỆỂỄêềếệểễ]", "e")
                .replaceAll("[ÌÍỊỈĨìíịỉĩ]", "i")
                .replaceAll("[ÒÓỌỎÕỐỒỘỔỖòóọỏõốồộổỗ]", "o")
                .replaceAll("[ÔỒỐỘỔỖôồốộổỗ]", "o")
                .replaceAll("[ƠỜỚỢỞỠơờớợởỡ]", "o")
                .replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮùúụủũưừứựửữ]", "u")
                .replaceAll("[ÝỲỴỶỸýỳỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[Đ]", "D")

                // Các ký tự khác như ç, ñ, và ký tự đặc biệt
                .replaceAll("ç", "c")   // ký tự ç
                .replaceAll("ñ", "n")   // ký tự ñ
                .replaceAll("ã", "a")   // ký tự ã
                .replaceAll("é", "e")   // ký tự é
                .replaceAll("ê", "e")   // ký tự ê
                .replaceAll("å", "a")   // ký tự å
                .replaceAll("ø", "o")   // ký tự ø
                .replaceAll("ý", "y")   // ký tự ý
                .replaceAll("Æ", "ae")  // ký tự Æ
                .replaceAll("ø", "o")   // ký tự ø
                .replaceAll("ø", "o")   // ký tự ø

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
        String input = "Đảm bảo rằng mọi ký tự đều được chuyển về chữ thường bằng .toLowerCase(Locale.ENGLISH)";
        String slug = toSlug(input);
        System.out.println(slug); // In kết quả slug
    }
}
