import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FibonacciTest {

    @Test
    void non_natural_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                System.out.println(Computer.fibonacci(0)));
    }
    @Test
    void fist_is_zero() {
        assertEquals(0, Computer.fibonacci(1));
    }
    @Test
    void second_is_one() {
        assertEquals(1, Computer.fibonacci(2));
    }
    @Test
    void third_is_one() {
        assertEquals(1, Computer.fibonacci(3));
    }
    @Test
    void fourth_is_two() {
        assertEquals(2, Computer.fibonacci(4));
    }
    @Test
    void fifth_is_three() {
        assertEquals(3, Computer.fibonacci(5));
    }
    @Test
    void sixth_is_five() {
        assertEquals(5, Computer.fibonacci(6));
    }



}