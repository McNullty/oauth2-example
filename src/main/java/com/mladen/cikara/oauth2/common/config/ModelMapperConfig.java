package com.mladen.cikara.oauth2.common.config;

import com.mladen.cikara.oauth2.authorization.server.security.model.RegisterUserDto;
import com.mladen.cikara.oauth2.authorization.server.security.model.User;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  private PropertyMap<RegisterUserDto, User.Builder> getRegisterUserDtoToUserPropertyMap() {
    final PropertyMap<RegisterUserDto, User.Builder> userMap =
        new PropertyMap<RegisterUserDto, User.Builder>() {

          @Override
          protected void configure() {
            map().email(this.source.getEmail());
            map().firstName(this.source.getFirstName());
            map().lastName(this.source.getLastName());
            map().password(this.source.getPassword());
          }
        };

    return userMap;
  }

  /**
   * Model mapper for converting User to UserDTO and vice versa.
   *
   * @return ModelMapper
   */
  @Bean
  public ModelMapper modelMapper() {
    final ModelMapper modelMapper = new ModelMapper();

    modelMapper.addMappings(getRegisterUserDtoToUserPropertyMap());

    return modelMapper;
  }
}
