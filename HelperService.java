import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuTaskService {

    @Autowired
    private SuService suService;

    public boolean processSuTask(LinkedHashMap<String, Object> requestData) {
        boolean suTaskRequired = false;

        // Extract fields from the LinkedHashMap
        String action = (String) requestData.get("action");
        String isMirrorAccount = (String) requestData.get("isMirrorAccount");
        List<LinkedHashMap<String, Object>> clientDetails = (List<LinkedHashMap<String, Object>>) requestData.get("clientDetails");
        LinkedHashMap<String, Object> accounts = (LinkedHashMap<String, Object>) requestData.get("accounts");
        List<LinkedHashMap<String, Object>> accountList = (List<LinkedHashMap<String, Object>>) accounts.get("accountList");
        LinkedHashMap<String, Object> swiftDetails = (LinkedHashMap<String, Object>) accounts.get("swiftDetails");
        LinkedHashMap<String, Object> cashStatements = (LinkedHashMap<String, Object>) swiftDetails.get("cashStatements");

        // Get MT tabs
        LinkedHashMap<String, String> mt940tab = (LinkedHashMap<String, String>) cashStatements.get("MT940tab");
        LinkedHashMap<String, String> mt950tab = (LinkedHashMap<String, String>) cashStatements.get("MT950tab");
        LinkedHashMap<String, String> mt950wtab = (LinkedHashMap<String, String>) cashStatements.get("MT950wtab");

        // Case 1: action is add/amend and isMirrorAccount is No
        if ((action.equalsIgnoreCase("add") || action.equalsIgnoreCase("amend")) && isMirrorAccount.equalsIgnoreCase("No")) {
            String bic = (String) clientDetails.get(0).get("receivingBic");

            // Call suService to check if BIC exists
            boolean bicExists = suService.checkBicExistsInSu(bic);

            // Check if MT940/950/950w is selected and has valid values
            boolean isValidMTTab = isMTTabValid(mt940tab) || isMTTabValid(mt950tab) || isMTTabValid(mt950wtab);

            // Check if at least one account exists
            boolean hasAccount = accountList != null && !accountList.isEmpty();

            if (bicExists && isValidMTTab && hasAccount) {
                suTaskRequired = true;
            }

        // Case 2: action is delete/add and isMirrorAccount is Yes
        } else if ((action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("add")) && isMirrorAccount.equalsIgnoreCase("Yes")) {
            List<String> accountCodes = accountList.stream()
                    .map(account -> (String) account.get("accountCode"))
                    .collect(Collectors.toList());

            String accountCodesCsv = String.join(",", accountCodes);

            // Call suService to check if BIC exists for accounts
            boolean bicExistsForAcc = suService.checkBicExistsForAcc(accountCodesCsv);

            if (bicExistsForAcc) {
                suTaskRequired = true;
            }
        }

        return suTaskRequired;
    }

    // Helper method to check if the MT tab is valid
    private boolean isMTTabValid(Map<String, String> mtTab) {
        if (mtTab == null) return false;
        String variation = mtTab.get("variation");
        String frequency = mtTab.get("frequency");
        return variation != null && !variation.isEmpty() && frequency != null && !frequency.isEmpty();
    }
}
