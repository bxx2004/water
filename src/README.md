# How to use?
* Step1
 
    Add the following dependencies to your project.
  * ```implementation("com.github.l42111996:kcp-base:1.6") ```
* Step2

  Call initialization function in your project main function.
    ```kotlin
    initWater(
              mode: Platform,
              port: Int = 54321,
              name_client: String="None",
              uuid_client: UUID = UUID.randomUUID(),
              host_client: String = "localhost"
             )
    ```

Have a good time :)