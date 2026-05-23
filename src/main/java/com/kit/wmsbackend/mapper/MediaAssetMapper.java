package com.kit.wmsbackend.mapper;

import com.kit.wmsbackend.entity.MediaAsset;
import com.kit.wmsbackend.feature.media.dto.MediaAssetResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface MediaAssetMapper {
    MediaAssetResponse toMediaAssetResponse(MediaAsset mediaAsset);
}
