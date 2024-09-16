import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MessageTypeChecker {

    public boolean checkMessageType(List<Map<String, Object>> listOfMaps) {
        boolean flag = false;
        
        for (Map<String, Object> map : listOfMaps) {
            // Check if the map contains the "msgType" key and the value is either "MT940" or "MT950"
            if (map.containsKey("msgType")) {
                Object msgTypeValue = map.get("msgType");
                if (msgTypeValue instanceof String) {
                    String msgType = (String) msgTypeValue;
                    if ("MT940".equals(msgType) || "MT950".equals(msgType)) {
                        flag = true;
                        break;  // No need to continue once we find a match
                    }
                }
            }
        }
        
        return flag;
    }
}
