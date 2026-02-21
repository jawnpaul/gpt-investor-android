import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.thejawnpaul.gptinvestor.core.api.KtorApiService
import com.thejawnpaul.gptinvestor.core.database.GPTInvestorDatabase
import com.thejawnpaul.gptinvestor.features.company.data.repository.CompanyRepository
import com.thejawnpaul.gptinvestor.features.company.domain.repository.ICompanyRepository
import com.thejawnpaul.gptinvestor.utils.RequestDispatcher
import com.thejawnpaul.gptinvestor.utils.makeTestKtorApiService
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before

class CompanyRepositoryTest {

    private lateinit var companyRepository: ICompanyRepository
    private lateinit var gptInvestorDatabase: GPTInvestorDatabase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        gptInvestorDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            GPTInvestorDatabase::class.java
        ).allowMainThreadQueries().build()
        
        companyRepository = CompanyRepository(
            makeTestKtorApiService(RequestDispatcher().successHandler),
            gptInvestorDatabase.companyDao()
        )
    }
}

