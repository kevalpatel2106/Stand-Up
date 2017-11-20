#remove log class
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
# Remove sout
-assumenosideeffects class java.io.PrintStream {
     public void println(%);
     public void println(**);
 }