package chitChat.chitChatApp;

import java.io.IOException;

public interface DataConsumer {
    void consume(String ...data) throws IOException;
}
