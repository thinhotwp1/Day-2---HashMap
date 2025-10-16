package one.year.senior.collisionkey;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CollisionHashMap {
    // Use Java 8, from Java 9 reflection will be limited
    public static void main(String[] args) {
        try {
            System.out.println("-----------------BEGIN------------------");
            HashMap<CollisionKey, String> map = new HashMap<>();
            CollisionKey key1 = new CollisionKey("1");
            CollisionKey key2 = new CollisionKey("2");

            map.put(key1, "Giá trị của 1");
            map.put(key2, "Giá trị của 2");

            // BƯỚC 1: Dùng reflection để lấy trường "table" (mảng bucket)
            Object[] table = getBuckets(map);

            /**
             * // Một HashMap với 16 buckets
             * // Key "1" và "2" có cùng index = 1 sau khi hash
             * table:
             * [0] -> null
             * [1] -> [Node(key="1", val="Giá trị của 1")] -> [Node(key="2", val="Giá trị của 2")]
             * [2] -> null
             * [3] -> null
             * ...
             * [15]-> null
             */

            // BƯỚC 3: Tính toán index của bucket mà các key sẽ rơi vào
            // Công thức: hashCode & (table.length - 1)
            int bucketIndex = key1.hashCode() & (table.length - 1);
            map.forEach((collisionKey, value) -> {
                System.out.println("HashCode của [" + collisionKey + ", " + value + "] là: " + collisionKey.hashCode());
                System.out.printf("--> Index bucket  của [" + collisionKey + ", " + value + "] được tính là: %d & (%d - 1) = %d%n%n", collisionKey.hashCode(), table.length, bucketIndex);

            });
            System.out.println("----------------------------------------");

            printBucketData(table, bucketIndex);
            System.out.println("\n==> KẾT LUẬN: Đã chứng minh được cả 2 object nằm trong cùng 1 bucket dưới dạng LinkedList.");
            System.out.println("----------------------------------------");

            System.out.println("Tiếp tục thêm các phần tử vào cùng 1 bucket tới khi vượt qua 8, bucket dạng LinkedList sẽ thành dạng TreeMap");
            CollisionKey key3 = new CollisionKey("3");
            CollisionKey key4 = new CollisionKey("4");
            CollisionKey key5 = new CollisionKey("5");
            CollisionKey key6 = new CollisionKey("6");
            CollisionKey key7 = new CollisionKey("7");
            CollisionKey key8 = new CollisionKey("8");
            CollisionKey key9 = new CollisionKey("9");

            map.put(key3, "Giá trị của 3");
            map.put(key4, "Giá trị của 4");
            map.put(key5, "Giá trị của 5");
            map.put(key6, "Giá trị của 6");
            map.put(key7, "Giá trị của 7");
            map.put(key8, "Giá trị của 8");
            map.put(key9, "Giá trị của 9");

            System.out.println("\n--- Lấy lại thông tin sau khi map có thể đã resize ---");
            Object[] newTable = getBuckets(map); // getBuckets sẽ in ra capacity mới là 32

            // Tính toán lại index với capacity mới
            int newBucketIndex = key1.hashCode() & (newTable.length - 1);
            System.out.printf("--> Index của bucket MỚI được tính là: %d & (%d - 1) = %d%n%n", key1.hashCode(), newTable.length, newBucketIndex);

            printBucketData(newTable, bucketIndex);
            System.out.println("\n==> KẾT LUẬN: Đã chứng minh được cả 9 object nằm trong cùng 1 bucket dưới dạng TreeMap.");

            System.out.println("------------------END-------------------");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Lỗi khi sử dụng Reflection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Object[] getBuckets(HashMap<CollisionKey, String> map) throws NoSuchFieldException, IllegalAccessException {
        Field tableField = HashMap.class.getDeclaredField("table");
        tableField.setAccessible(true); // Cho phép truy cập trường private

        // BƯỚC 2: Lấy đối tượng mảng từ instance 'map'
        // Mảng này có kiểu là Node<K,V>[]
        Object[] table = (Object[]) tableField.get(map);
        System.out.printf("Capacity của map (table.length) hiện tại là: %d%n\n", table.length);
        return table;
    }

    private static void printBucketData(Object[] table, int bucketIndex) throws NoSuchFieldException, IllegalAccessException {
        // BƯỚC 4: Lấy bucket (Node đầu tiên của LinkedList) tại index đó
        Object bucketNode = table[bucketIndex];

        if (bucketNode != null) {
            System.out.printf("Đã tìm thấy bucket tại index %d. Nội dung bên trong:%n", bucketIndex);

            // BƯỚC 5: Dùng reflection để duyệt qua LinkedList (qua trường "next")
            Object currentNode = bucketNode;
            Field nextField = currentNode.getClass().getDeclaredField("next");
            nextField.setAccessible(true);

            int nodeCount = 1;
            while (currentNode != null) {
                System.out.printf("   - Node %d: %s%n", nodeCount++, currentNode);
                // Lấy node tiếp theo trong chuỗi liên kết
                currentNode = nextField.get(currentNode);
            }

        } else {
            System.out.printf("Không tìm thấy bucket nào tại index %d.%n", bucketIndex);
        }
    }
}