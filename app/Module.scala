import com.google.inject.AbstractModule
import services.AccountService

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[AccountService]).asEagerSingleton()
  }

}
