package morben.springframework.sfgpetclinic.controllers;

import morben.springframework.sfgpetclinic.fauxspring.BindingResult;
import morben.springframework.sfgpetclinic.fauxspring.Model;
import morben.springframework.sfgpetclinic.model.Owner;
import morben.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    private static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    private static final String REDIRECT_OWNERS_5 = "redirect:/owners/5";

    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController ownerController;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void setUp() {
        given(ownerService.findAllByLastNameLike(stringArgumentCaptor.capture()))
                .willAnswer(invocation -> {

                    List<Owner> ownerList = new ArrayList<>();
                    String name = invocation.getArgument(0);

                    if (name.equals("%Buck%")) {
                        ownerList.add(new Owner(1l, "Joe", "Buck"));
                        return ownerList;
                    } else if (name.equals("%DontFindMe%")) {
                        return ownerList;
                    } else if (name.equals("%FindMe%")) {
                        ownerList.add(new Owner(1l, "Joe", "Buck"));
                        ownerList.add(new Owner(2l, "Joe2", "Buck2"));
                        return ownerList;
                    }

                    throw new RuntimeException("Invalid Argument");
                });
    }

    @Test
    void processFindFormWildCardStringAnnotation() {
        //given
        Owner owner = new Owner(1l, "Joe", "Buck");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%Buck%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("redirect:/owners/1").isEqualToIgnoringCase(viewName);
    }

    @Test
    void processFindFormWildCardNotFound() {
        //given
        Owner owner = new Owner(1l, "Joe", "DontFindMe");

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult, null);

        //then
        assertThat("%DontFindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/findOwners").isEqualToIgnoringCase(viewName);
    }

    @Test
    void processFindFormWildcardFound() {
        //given
        Owner owner = new Owner(1l, "Joe", "FindMe");
        InOrder inOrder = inOrder(ownerService, model);

        //when
        String viewName = ownerController.processFindForm(owner, bindingResult,
                model);

        //then
        assertThat("%FindMe%").isEqualToIgnoringCase(stringArgumentCaptor.getValue());
        assertThat("owners/ownersList").isEqualToIgnoringCase(viewName);

        //inorder asserts
        inOrder.verify(ownerService).findAllByLastNameLike(anyString());
        inOrder.verify(model).addAttribute(anyString(), anyList());
    }

//    @Test
//    void processCreationFormHasErrors() {
//        //given
//        Owner owner = new Owner(1l, "Jim", "Bob");
//        given(bindingResult.hasErrors()).willReturn(true);
//
//        //when
//        String viewName = ownerController.processCreationForm(owner, bindingResult);
//
//        //then
//        assertTrue(viewName.equalsIgnoreCase(OWNERS_CREATE_OR_UPDATE_OWNER_FORM));
//        assertThat(viewName).isEqualToIgnoringCase(OWNERS_CREATE_OR_UPDATE_OWNER_FORM);
//    }
//
//    @Test
//    void processCreationFormNoErrors() {
//        //given
//        Owner owner = new Owner(5l, "Jim", "Bob");
//        given(bindingResult.hasErrors()).willReturn(false);
//        given(ownerService.save(any())).willReturn(owner);
//
//        //when
//        String viewName = ownerController.processCreationForm(owner, bindingResult);
//
//        //then
//        assertThat(viewName).isEqualToIgnoringCase(REDIRECT_OWNERS_5);
//    }
}