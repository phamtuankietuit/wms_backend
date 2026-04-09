package com.kit.wmsbackend.feature.variant.service;

import com.kit.wmsbackend.entity.Variant;
import com.kit.wmsbackend.service.BaseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VariantServiceImpl extends BaseQueryService<Variant> implements VariantService {
}
