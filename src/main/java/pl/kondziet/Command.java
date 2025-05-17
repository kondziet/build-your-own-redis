package pl.kondziet;

import java.util.List;

public record Command(CommandType type, List<String> arguments) {
}
