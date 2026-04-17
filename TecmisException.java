public class TecmisException extends Exception {
    public TecmisException(String message) {
        super(message);
    }
}

class RecordNotFoundException extends TecmisException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}

class DuplicateRecordException extends TecmisException {
    public DuplicateRecordException(String message) {
        super(message);
    }
}

class InvalidDataException extends TecmisException {
    public InvalidDataException(String message) {
        super(message);
    }
}

class DatabaseOperationException extends TecmisException {
    public DatabaseOperationException(String message) {
        super(message);
    }
}