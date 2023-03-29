package com.majid2851.clean_architecture.business.domain.util

interface EntityMapper<Entity,DomainModel>
{
    fun mapFromEntity(entity: Entity):DomainModel

    fun mapToEntity(domainModel: DomainModel):Entity







}