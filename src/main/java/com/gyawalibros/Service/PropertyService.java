package com.gyawalibros.Service;

import com.gyawalibros.Repository.PropertyRepository;
import com.gyawalibros.Repository.UserRepository;
import com.gyawalibros.domain.Property;
import com.gyawalibros.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Property> getAllProperties(){
        List<Property> properties = (List<Property>) propertyRepository.findAll();
        return properties;
    }

    public void addProperty(Property property, UserDetailsImpl userDetail) {
        User user = userDetail.getUser();

        Date currentDate = Calendar.getInstance().getTime();
        property.setCreatedDate(currentDate);
        property.setPropertyAids("3 Months");
        property.setUser(user);
        propertyRepository.save(property);
    }

    public List<Property> findUserProperties(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Iterable<Property> properties = propertyRepository.findAllByUser(user);
        return (List<Property>) properties;
    }

    public Property getUserProperty(Long propertyId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Property property = propertyRepository.findOne(propertyId);
        if(property.getUser().getId() == user.getId()){
            return property;
        }

        return null;
    }

    public void updatePropertyByUser(Property property, UserDetailsImpl userDetails) {
        Property propertyFromRepo = propertyRepository.findOne(property.getId());
        property.setUser(propertyFromRepo.getUser());

        User user = userDetails.getUser();
        if(propertyFromRepo.getUser().getId() == user.getId()){
            propertyRepository.save(property);
        }
    }

    public void removeUserProperty(Long propertyId, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Property property = propertyRepository.findOne(propertyId);
        if(property.getUser().getId() == user.getId()){
            propertyRepository.delete(propertyId);
        }
    }

    public void sold(Property property){
        property.setSold(true);
        propertyRepository.save(property);
    }
}
