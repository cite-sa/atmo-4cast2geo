package gr.cite.atmo4cast2geo.exception;

import java.nio.file.FileAlreadyExistsException;

public class ExceptionParser {

    private enum Exceptions {
        RUNTIME_EXCEPTION(RuntimeException.class.getSimpleName()),
        FILE_ALREADY_EXISTS_EXCEPTION(FileAlreadyExistsException.class.getSimpleName()),
        EXCEPTION(Exception.class.getSimpleName());
        public final String value;

        Exceptions(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public static Exceptions fromValue(String value) {
            for (Exceptions exceptions: Exceptions.values()) {
                if (value.equals(exceptions.getValue())) {
                    return exceptions;
                }
            }
            return EXCEPTION;
        }
    }

    public static String parseMessage(Exception e) {
        switch (Exceptions.fromValue(e.getClass().getSimpleName())) {
            case RUNTIME_EXCEPTION:
                if (e.getCause() != null) {
                    return e.getCause().getMessage();
                }
                break;
            case FILE_ALREADY_EXISTS_EXCEPTION:
                return "File Already exists on temporary storage, please wait a little before sending again that file";
            default:
                return e.getMessage();
        }
        return e.getMessage();
    }
}
