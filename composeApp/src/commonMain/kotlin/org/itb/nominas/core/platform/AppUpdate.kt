package org.itb.nominas.core.platform

expect fun appIsLastVersion(lastVersion: Int): Boolean
expect fun openPlayStoreOrAppStore()