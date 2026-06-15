package kz.nurdaulet.service;

import kz.nurdaulet.dto.RestaurantCreateDto;
import kz.nurdaulet.entity.Restaurant;

public interface RestaurantService {
    Restaurant create(RestaurantCreateDto dto, Long userId);
}
