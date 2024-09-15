import java.util.List;

@Service
public class SuResService {

    public boolean checkProductType(List<SuRes> suResList) {
        // Initialize the flag to false
        boolean flag = false;

        // Iterate over the list of SuRes objects
        for (SuRes suRes : suResList) {
            String productType = suRes.getProductType();

            // Check if the productType matches any of the required values
            if ("MT950I".equalsIgnoreCase(productType) || 
                "MT950".equalsIgnoreCase(productType) || 
                "MT940".equalsIgnoreCase(productType) || 
                "MT940I".equalsIgnoreCase(productType)) {
                
                // Set flag to true if any match is found
                flag = true;
                break; // No need to continue, as we already found a match
            }
        }

        return flag;
    }
}
