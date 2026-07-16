package com.infy.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.infy.validator.Validator;
import com.infy.dao.MobileServiceDAO;
import com.infy.dao.MobileServiceDAOImpl;
import com.infy.exception.MobileServiceException;
import com.infy.model.ServiceReport;
import com.infy.model.ServiceRequest;
import com.infy.model.Status;

public class MobileServiceImpl implements MobileService {

    private MobileServiceDAOImpl mobileServiceDAOImpl = new MobileServiceDAOImpl();
    private Validator validator = new Validator();

    @Override
    public ServiceRequest registerRequest(ServiceRequest service) throws MobileServiceException {
        // Validate the ServiceRequest object
        validator.validate(service);

        // Calculate the estimate cost
        Float cost = calculateEstimateCost(service.getIssues());

        // If cost is less than or equal to 0, throw exception
        if (cost < 0) {
            throw new MobileServiceException("Sorry, we do not provide that service.");
        }

        // Set the serviceFee
        service.setServiceFee(cost);

        // Set status to ACCEPTED and timeOfRequest to current date/time
        service.setStatus(Status.ACCEPTED);
        service.setTimeOfRequest(LocalDateTime.now());

        // FIXED: Call DAO method, NOT itself
        return mobileServiceDAOImpl.registerRequest(service);
    }

    @Override
    public Float calculateEstimateCost(List<String> issues) {
        Float totalCost = 0.0f;

        if (issues != null) {
            for (String issue : issues) {
                if (issue != null) {
                    String issueUpperCase = issue.toUpperCase();
                    switch (issueUpperCase) {
                        case "BATTERY":
                            totalCost += 10.0f;
                            break;
                        case "CAMERA":
                            totalCost += 5.0f;
                            break;
                        case "SCREEN":
                            totalCost += 15.0f;
                            break;
                        default:
                            return 0.0f;
                    }
                }
            }
        }

        return totalCost;
    }

    @Override
    public List<ServiceReport> getServices(Status status) throws MobileServiceException {
        List<ServiceReport> serviceReports = new ArrayList<>();

        // Get all service requests from DAO layer
        List<ServiceRequest> allRequests = mobileServiceDAOImpl.getServices();

        // Filter based on status and populate ServiceReport list
        for (ServiceRequest request : allRequests) {
            if (request.getStatus() == status) {
                ServiceReport report = new ServiceReport(
                        request.getServiceId(),
                        request.getBrand(),
                        request.getIssues(),
                        request.getServiceFee()
                );
                serviceReports.add(report);
            }
        }

        // If no records found, throw exception
        if (serviceReports.isEmpty()) {
            throw new MobileServiceException("Sorry, we did not find any records for your query.");
        }

        return serviceReports;
    }
}