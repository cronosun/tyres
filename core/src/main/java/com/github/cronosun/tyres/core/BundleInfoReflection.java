package com.github.cronosun.tyres.core;

final class BundleInfoReflection {

    private BundleInfoReflection() {
    }

    public static BundleInfo reflect(Class<?> bundleClass, TyResImplementation implementation) {
        var baseName = getBaseName(bundleClass);
        return new BundleInfo(bundleClass, baseName, implementation);
    }

    private static BaseName getBaseName(Class<?> bundleClass) {
        var renamePackageAnnotation = bundleClass.getAnnotation(RenamePackage.class);
        var renameNameAnnotation = bundleClass.getAnnotation(RenameName.class);

        if (renamePackageAnnotation == null && renameNameAnnotation == null) {
            // 99% case
            return BaseName.fromClass(bundleClass);
        }

        final String packageName;
        if (renamePackageAnnotation == null) {
            packageName = bundleClass.getPackageName();
        } else {
            packageName = renamePackageAnnotation.value();
        }

        final String name;
        if (renameNameAnnotation == null) {
            name = bundleClass.getSimpleName();
        } else {
            name = renameNameAnnotation.value();
        }

        return BaseName.fromPackageAndName(packageName, name);
    }

}
