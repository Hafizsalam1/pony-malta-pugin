package com.github.hafizsalam1.ponymaltapugin

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.malta.plugin.analyzer.AffectedTableAnalyzer
import com.malta.plugin.resolver.RepositoryResolver
import com.malta.plugin.resolver.TableResolver
import com.malta.plugin.resolver.OperationResolver
import com.malta.plugin.util.OperationType

class TableResolverTest : LightJavaCodeInsightFixtureTestCase() {

    fun testShouldResolveTableFromInheritedRepositoryInterface() {
        myFixture.configureByText(
            "TestRepository.java",
            """
package test;

interface JpaRepository<T, ID> {
}

interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
}

interface UserRepository extends BaseRepository<User, Long> {
}

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "users")
class User {
}
""".trimIndent()
        )

        val javaPsiFacade = JavaPsiFacade.getInstance(project)
        val repositoryClass = javaPsiFacade.findClass("test.UserRepository", GlobalSearchScope.allScope(project))

        assertNotNull(repositoryClass)

        val repositoryResolver = RepositoryResolver()
        val tableResolver = TableResolver()

        assertTrue(repositoryResolver.isRepository(repositoryClass as PsiClass))
        assertEquals("users", tableResolver.resolve(repositoryClass))
    }

    fun testShouldRecognizeJpaSaveMethodsAsWriteOperations() {
        myFixture.configureByText(
            "TestRepository.java",
            """
package test;

interface CrudRepository<T, ID> {
    T save(T entity);
    T saveAndFlush(T entity);
}

interface UserRepository extends CrudRepository<User, Long> {
}

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "users")
class User {
}
""".trimIndent()
        )

        val javaPsiFacade = JavaPsiFacade.getInstance(project)
        val repositoryClass = javaPsiFacade.findClass("test.UserRepository", GlobalSearchScope.allScope(project))
        val saveMethod = repositoryClass?.findMethodsByName("save", false)?.firstOrNull()
        val saveAndFlushMethod = repositoryClass?.findMethodsByName("saveAndFlush", false)?.firstOrNull()

        assertNotNull(repositoryClass)
        assertNotNull(saveMethod)
        assertNotNull(saveAndFlushMethod)

        val operationResolver = OperationResolver()
        assertEquals(OperationType.INSERT, operationResolver.resolve(saveMethod))
        assertEquals(OperationType.INSERT, operationResolver.resolve(saveAndFlushMethod))
    }

    fun testShouldResolveTableForInheritedSaveMethodCalls() {
        myFixture.configureByText(
            "TestRepository.java",
            """
package test;

interface CrudRepository<T, ID> {
    T save(T entity);
}

interface UserRepository extends CrudRepository<User, Long> {
}

@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "users")
class User {
}

class UserService {
    private UserRepository userRepository;

    void saveUser(User user) {
        userRepository.save(user);
    }
}
""".trimIndent()
        )

        val javaPsiFacade = JavaPsiFacade.getInstance(project)
        val serviceClass = javaPsiFacade.findClass("test.UserService", GlobalSearchScope.allScope(project))
        val method = serviceClass?.findMethodsByName("saveUser", false)?.firstOrNull()

        assertNotNull(serviceClass)
        assertNotNull(method)

        val analyzer = AffectedTableAnalyzer()
        val accesses = analyzer.analyze(method!!)

        assertTrue(accesses.isNotEmpty())
        assertEquals("users", accesses.first().tableName)
        assertEquals(OperationType.INSERT, accesses.first().operation)
    }
}
