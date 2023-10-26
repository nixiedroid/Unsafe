public class Samples {
    public void swallowException(){
        try {
            throw new RuntimeException();
        } finally {
            return;
        }
    }
}
