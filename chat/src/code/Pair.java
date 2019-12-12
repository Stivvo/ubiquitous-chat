package code;

public class Pair<K, V> {
    public final K key;
    public final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        // casting to whatever pair
        Pair<?, ?> pair = (Pair<?, ?>) o;

        return key.equals(pair.key) == value.equals(pair.value);
    }

    public String toString() {
        return "(" + key.toString() + ", " + value.toString() + ")";
    }

    public static <K, V> Pair<K, V> of(K k, V v) {
        return new Pair<>(k, v);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

}
