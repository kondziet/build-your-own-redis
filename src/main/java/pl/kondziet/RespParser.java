package pl.kondziet;

import pl.kondziet.ParseResult.*;

import java.util.ArrayList;

import static pl.kondziet.Protocol.*;

public class RespParser {

    private String source;
    private int position;
    private ParseResult failure;

    public ParseResult parse(String source) {
        this.source = source;
        position = 0;
        failure = null;

        RespType element = parseElement();
        return failure != null ? failure : new Complete(element);
    }

    private RespType parseElement() {
        if (position >= source.length()) {
            setIncomplete();
            return null;
        }
        char typeIndicator = source.charAt(position);

        return switch (typeIndicator) {
            case ARRAY -> parseArray();
            case BULK_STRING -> parseBulkString();
            default -> {
                setError("unknown data type '%s' at position %d".formatted(typeIndicator, position));
                yield null;
            }
        };
    }

    private Array parseArray() {
        position++;

        Integer numberOfElements = consumeInt();
        if (numberOfElements == null) {
            return null;
        }

        ArrayList<RespType> elements = new ArrayList<>(numberOfElements);
        for (int i = 0; i < numberOfElements; i++) {
            RespType element = parseElement();
            if (element == null) {
                return null;
            }
            elements.add(element);
        }

        return new Array(elements);
    }

    private BulkString parseBulkString() {
        position++;

        Integer lengthOfString = consumeInt();
        if (lengthOfString == null) {
            return null;
        }

        int endIndex = source.indexOf(CRLF, position);
        if (endIndex == -1 || endIndex < position + lengthOfString) {
            setIncomplete();
            return null;
        }

        String content = source.substring(position, position + lengthOfString);
        position += lengthOfString + CRLF.length();

        return new BulkString(content);
    }

    private Integer consumeInt() {
        int crlfIndex = source.indexOf(CRLF, position);
        if (crlfIndex == -1) {
            setIncomplete();
            return null;
        }

        String numberLiteral = source.substring(position, crlfIndex);
        int value;
        try {
            value = Integer.parseInt(numberLiteral);
        } catch (NumberFormatException e) {
            setError("invalid integer '%s' at position %d".formatted(numberLiteral, position));
            return null;
        }

        position += numberLiteral.length() + CRLF.length();
        return value;
    }

    private void setIncomplete() {
        failure = new Incomplete();
    }

    private void setError(String message) {
        failure = new Complete(new SimpleError(message));
    }
}
