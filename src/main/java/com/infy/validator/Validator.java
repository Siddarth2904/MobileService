package com.infy.validator;

import java.util.List;
import java.util.regex.Pattern;

import com.infy.exception.MobileServiceException;
import com.infy.model.ServiceRequest;

public class Validator {

    // Pre-compile patterns to avoid compilation issues
    private static final Pattern BRAND_PATTERN = Pattern.compile("^[A-Z][A-Za-z]+$");
    private static final Pattern IMEI_PATTERN = Pattern.compile("^\\d{16}$");
    private static final Pattern CONTACT_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern WORD_PATTERN = Pattern.compile("^[A-Z][a-z]+$");

    public void validate(ServiceRequest service) throws MobileServiceException {
        if (!isValidBrand(service.getBrand())) {
            throw new MobileServiceException("Sorry! we do not provide service for this brand");
        }
        if (!isValidIssues(service.getIssues())) {
            throw new MobileServiceException("Please provide the device only if there are issues.");
        }
        if (!isValidIMEINumber(service.getiMEINumber())) {
            throw new MobileServiceException("Sorry! we're not able to detect the IMEI number for this device");
        }
        if (!isValidContactNumber(service.getContactNumber())) {
            throw new MobileServiceException("Please provide a valid contact number");
        }
        if (!isValidCustomerName(service.getCustomerName())) {
            throw new MobileServiceException("Please provide a valid customer name");
        }
    }

    public Boolean isValidBrand(String brand){
        if (brand == null || brand.isEmpty()) {
            return false;
        }
        return BRAND_PATTERN.matcher(brand).matches();
    }

    public Boolean isValidIssues(List<String> issues) {
        return issues != null && !issues.isEmpty();
    }

    public Boolean isValidIMEINumber(Long iMEINumber) {
        if (iMEINumber == null) {
            return false;
        }
        return IMEI_PATTERN.matcher(String.valueOf(iMEINumber)).matches();
    }

    public Boolean isValidContactNumber(Long contactNumber) {
        if (contactNumber == null) {
            return false;
        }
        String str = String.valueOf(contactNumber);

        if (!CONTACT_PATTERN.matcher(str).matches()) {
            return false;
        }

        // Check if all digits are not the same
        char firstDigit = str.charAt(0);
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) != firstDigit) {
                return true;
            }
        }
        return false;
    }

    public Boolean isValidCustomerName(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            return false;
        }

        String trimmed = customerName.trim();

        // Check for multiple consecutive spaces
        if (trimmed.contains("  ")) {
            return false;
        }

        String[] words = trimmed.split(" ");

        for (String word : words) {
            if (word.isEmpty()) {
                return false;
            }
            if (!WORD_PATTERN.matcher(word).matches()) {
                return false;
            }
        }
        return true;
    }
}