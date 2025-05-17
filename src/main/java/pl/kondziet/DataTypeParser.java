package pl.kondziet;

import pl.kondziet.Outcome.Success;

import java.util.ArrayList;

import static pl.kondziet.Protocol.*;

public class DataTypeParser {

    private int position;
    private String source;

    public Outcome<DataType> parse(String source) {
        position = 0;
        this.source = source;

        return new Success<>(parseDataType());
    }

    private DataType parseDataType() {
        char typeIndicator = source.charAt(position);

        return switch (typeIndicator) {
            case ARRAY -> parseArray();
            case BULK_STRING -> parseBulkString();
            default -> null; // throw error("unknown data type '%s' at position %d".formatted(type, position));
        };
    }

    private Array parseArray() {
        position++;

        int numberOfElements = consumeInt();
        ArrayList<DataType> elements = new ArrayList<>(numberOfElements);
        for (int i = 0; i < numberOfElements; i++) {
            elements.add(parseDataType());
        }

        return new Array(elements);
    }

    private BulkString parseBulkString() {
        position++;

        int lengthOfString = consumeInt();
        String content = source.substring(position, source.indexOf(CRLF, position));
        position += lengthOfString + CRLF.length();

        return new BulkString(content);
    }

    private int consumeInt() {
        int crlfIndex = source.indexOf(CRLF, position);
        String numberLiteral = source.substring(position, crlfIndex);
        int value = Integer.parseInt(numberLiteral);

        position += numberLiteral.length() + CRLF.length();
        return value;
    }
}
