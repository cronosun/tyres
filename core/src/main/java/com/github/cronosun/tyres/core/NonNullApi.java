package com.github.cronosun.tyres.core;

import java.lang.annotation.ElementType;
import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import kotlin.annotations.jvm.MigrationStatus;
import kotlin.annotations.jvm.UnderMigration;

@Nonnull
@TypeQualifierDefault({ ElementType.METHOD, ElementType.PARAMETER })
@UnderMigration(status = MigrationStatus.WARN)
public @interface NonNullApi {
}
