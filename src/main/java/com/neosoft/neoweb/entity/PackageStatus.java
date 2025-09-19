package com.neosoft.neoweb.entity;

public enum PackageStatus {

    DRAFT,      // Admin tarafından yüklendi ama henüz aktif değil
    ACTIVE,     // Kullanıcılara dağıtılacak güncel sürüm
    DEPRECATED  // Eski sürüm, artık kullanılmıyor

}
