package com.example.demo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// ####################
import com.example.demo.controllers.TestCartController;
import com.example.demo.controllers.TestItemController;
import com.example.demo.controllers.TestOrderController;
import com.example.demo.controllers.TestUserController;
import com.example.demo.exceptions.TestExceptionHandler;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		TestCartController.class, TestItemController.class,
		TestOrderController.class, TestUserController.class,
		TestExceptionHandler.class
})
public class SareetaApplicationTests {

}
