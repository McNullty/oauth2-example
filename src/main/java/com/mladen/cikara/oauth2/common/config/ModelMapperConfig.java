package com.mladen.cikara.oauth2.common.config;

import com.mladen.cikara.oauth2.authorization.server.security.model.User;
import com.mladen.cikara.oauth2.authorization.server.security.model.UserDto;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  private PropertyMap<UserDto, User> getUserDtoToUserPropertyMap() {
    final PropertyMap<UserDto, User> userMap = new PropertyMap<UserDto, User>() {

      @Override
      protected void configure() {
        map().setClearTextPassword(source.getPassword());
        skip().setPassword(null);
      }
    };

    return userMap;
  }

  @Bean
  public ModelMapper modelMapper() {
    final ModelMapper modelMapper = new ModelMapper();

    modelMapper.addMappings(getUserDtoToUserPropertyMap());

    return modelMapper;
  }
}
