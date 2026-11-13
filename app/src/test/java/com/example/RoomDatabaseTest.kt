import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.SimulationLog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testSimulationLogInsertAndQuery() = runBlocking {
        val dao = db.simulationLogDao()
        val log = SimulationLog(
            siteName = "Shackleton Connecting Ridge",
            timestampMillis = System.currentTimeMillis(),
            durationHours = 24.0,
            distanceKm = 14.8,
            energyConsumedKWh = 18.5,
            batteryRemainingPercent = 65.0,
            volatilesExtractedKg = 120.0,
            statusSummary = "NOMINAL TRAVERSE"
        )

        dao.insertLog(log)

        val allLogs = dao.getAllLogs().first()
        assertEquals(1, allLogs.size)
        assertEquals("Shackleton Connecting Ridge", allLogs[0].siteName)
        assertEquals(65.0, allLogs[0].batteryRemainingPercent, 0.01)
    }
}

