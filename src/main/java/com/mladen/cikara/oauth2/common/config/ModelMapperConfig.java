package com.mladen.cikara.oauth2.common.config;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  private PropertyMap<RegisterUserDto, User.Builder> getRegisterUserDtoToUserPropertyMap() {
    final PropertyMap<RegisterUserDto, User.Builder> userMap = new PropertyMap<RegisterUserDto, User.Builder>() {

      @Override
      protected void configure() {
        map().email(source.getEmail());
        map().firstName(source.getFirstName());
        map().lastName(source.getLastName());
        map().password(source.getPassword());
      }
    };

    return userMap;
  }

  @Bean
  public ModelMapper modelMapper() {
    final ModelMapper modelMapper = new ModelMapper();

    modelMapper.addMappings(getRegisterUserDtoToUserPropertyMap());

    return modelMapper;
  }
}
