package vn.civilpro.mapper;


import org.mapstruct.Mapper;
import vn.civilpro.model.dto.UserDto;
import vn.civilpro.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDto> {

}
