package se.umu.cs.c22jwt.garagelog

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import se.umu.cs.c22jwt.garagelog.database.ReminderDao
import se.umu.cs.c22jwt.garagelog.database.ServiceDao
import se.umu.cs.c22jwt.garagelog.database.VehicleDao
import se.umu.cs.c22jwt.garagelog.database.VehicleDatabase
import se.umu.cs.c22jwt.garagelog.database.VehicleRepository
import javax.inject.Singleton

/**
 * App module for providing objects for the Dagger Hilt dependency injection.
 *
 * @author Jonatan Wincent (c22jwt@cs.umu.se)
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVehicleDatabase(@ApplicationContext context: Context): VehicleDatabase =
        Room.databaseBuilder(context, VehicleDatabase::class.java, "vehicle-database")
            .fallbackToDestructiveMigration(true).build()

    @Provides
    fun provideVehicleDao(vehicleDatabase: VehicleDatabase): VehicleDao =
        vehicleDatabase.vehicleDao()

    @Provides
    fun provideServiceDao(vehicleDatabase: VehicleDatabase): ServiceDao =
        vehicleDatabase.serviceDao()

    @Provides
    fun provideReminderDao(vehicleDatabase: VehicleDatabase): ReminderDao =
        vehicleDatabase.reminderDao()

    @Provides
    @Singleton
    fun provideVehicleRepository(
        vehicleDao: VehicleDao, serviceDao: ServiceDao, reminderDao: ReminderDao
    ): VehicleRepository = VehicleRepository(vehicleDao, serviceDao, reminderDao)
}