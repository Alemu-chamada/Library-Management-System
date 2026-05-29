package library.io;

@FunctionalInterface
interface LineParser<T> {
    T parse(String[] parts, String originalLine) throws Exception;
}