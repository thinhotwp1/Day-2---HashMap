package one.year.senior.collisionkey;

import java.util.Objects;

/**
 * Lớp này được thiết kế để cố tình tạo ra xung đột (collision) hashCode.
 * Mọi đối tượng của lớp này sẽ có cùng một hashCode.
 */
class CollisionKey {
    private String name;

    public CollisionKey(String name) {
        this.name = name;
    }

    // Ghi đè phương thức equals()
    // Hai đối tượng CollisionKey chỉ bằng nhau khi chúng có cùng "name".
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollisionKey that = (CollisionKey) o;
        return Objects.equals(name, that.name);
    }

    // *** ĐIỂM QUAN TRỌNG NHẤT ***
    // Ghi đè phương thức hashCode() để LUÔN LUÔN trả về cùng một giá trị.
    // Điều này đảm bảo mọi đối tượng sẽ có cùng hashCode.
    @Override
    public int hashCode() {
        return 1; // Luôn trả về 1 để tạo ra xung đột
    }

    @Override
    public String toString() {
        return "Key[" + name + "]";
    }
}