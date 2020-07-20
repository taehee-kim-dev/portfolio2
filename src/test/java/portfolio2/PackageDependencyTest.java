package portfolio2;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = Application.class)
public class PackageDependencyTest {

    private static final String ACCOUNT = "..module.account..";
    private static final String POST = "..module.post..";
    private static final String TAG = "..module.tag..";
    private static final String NOTIFICATION = "..module.notification..";
    private static final String JAVA_LANG_OBJECT = "..java.lang..";

    @ArchTest
    ArchRule modulePackageRule = classes().that().resideInAPackage("portfolio2.module..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("portfolio2.module..");

    @ArchTest
    ArchRule postPackageRuleAccessedRule = classes().that().resideInAPackage(POST)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(POST);

    @ArchTest
    ArchRule postPackageAccessRule = classes().that().resideInAPackage(POST)
            .should().accessClassesThat().resideInAnyPackage(POST, JAVA_LANG_OBJECT, ACCOUNT, TAG);

    @ArchTest
    ArchRule accountPackageAccessRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(ACCOUNT, JAVA_LANG_OBJECT, TAG);

    @ArchTest
    ArchRule notificationPackageAccessRule = classes().that().resideInAPackage(NOTIFICATION)
            .should().accessClassesThat().resideInAnyPackage(NOTIFICATION, JAVA_LANG_OBJECT, ACCOUNT, TAG);

    @ArchTest
    ArchRule cycleCheck = slices().matching("portfolio2.module.(*)..")
            .should().beFreeOfCycles();
}