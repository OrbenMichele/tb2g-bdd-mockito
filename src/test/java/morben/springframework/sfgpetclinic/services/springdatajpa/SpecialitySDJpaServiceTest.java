package morben.springframework.sfgpetclinic.services.springdatajpa;

import morben.springframework.sfgpetclinic.model.Speciality;
import morben.springframework.sfgpetclinic.repositories.SpecialtyRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialitySDJpaServiceTest {

    @Mock(lenient = true)
    SpecialtyRepository specialtyRepository;

    @InjectMocks
    SpecialitySDJpaService service;

    @Test
    void testDeleteByObject() {

        //given
        Speciality speciality = new Speciality();

        //when
        service.delete(speciality);

        //then
        then(specialtyRepository).should().delete(any(Speciality.class));
        //verify(specialtyRepository).delete(any(Speciality.class));
    }

    @Test
    void findByIdTest() {

        //given
        Speciality speciality = new Speciality();

        // to BDD style
        given(specialtyRepository.findById(1L)).willReturn(Optional.of(speciality));

        //when
        Speciality foundSpecialty = service.findById(1L);

        //then
        assertThat(foundSpecialty).isNotNull();
        then(specialtyRepository).should(times(1)).findById(anyLong());
        then(specialtyRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    void deleteById() {
        //given - none

        //when
        service.deleteById(1l);
        service.deleteById(1l);

        //then
        then(specialtyRepository).should(times(2)).deleteById(1l);
    }

    @Test
    void deleteByIdAtLeast() {
        //given - none

        //when
        service.deleteById(1l);
        service.deleteById(1l);

        //then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1l);
    }

    @Test
    void deleteByIdAtMost() {
        //given - none

        //when
        service.deleteById(1l);
        service.deleteById(1l);

        //then
        then(specialtyRepository).should(atMost(5)).deleteById(1l);
    }

    @Test
    void deleteByIdNever() {
        //given - none

        //when
        service.deleteById(1l);
        service.deleteById(1l);

        //then
        then(specialtyRepository).should(atLeastOnce()).deleteById(1l);
        then(specialtyRepository).should(never()).deleteById(5L);
    }

    @Test
    void testDelete() {

        //when
        service.delete((new Speciality()));

        //then
        then(specialtyRepository).should().delete(any(Speciality.class));
    }

    @Test
    void testThrow() {

        doThrow(new RuntimeException("Boom")).when(specialtyRepository).delete(any());

        assertThrows(RuntimeException.class, () -> specialtyRepository.delete(new Speciality()));
        verify(specialtyRepository).delete(any());
    }

    @Test
    void testFindByIdThrows() {
        //given
        given(specialtyRepository.findById(1l)).willThrow(new RuntimeException("Boom"));

        //when
        assertThrows(RuntimeException.class, () -> service.findById(1l));

        //then
        then(specialtyRepository).should().findById(1l);
    }

    @Test
    void testDeleteBDD() {

        //given
        willThrow(new RuntimeException("Boom")).given(specialtyRepository).delete(any());

        //when
        assertThrows(RuntimeException.class, () -> service.delete(new Speciality()));

        //then
        then(specialtyRepository).should().delete(any());
    }

    @Test
    void testSaveLambda() {
        //given
        final String MATCH_ME = "MATCH_ME";
        Speciality speciality = new Speciality();
        speciality.setDescription(MATCH_ME);

        Speciality savedSpecialty = new Speciality();
        savedSpecialty.setId(1l);

        given(specialtyRepository.save(argThat(argument
                -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpecialty);

        //when
        Speciality returnedSpecialty = service.save(speciality);

        //then
        assertThat(returnedSpecialty.getId()).isEqualTo(1l);
    }

    @Test
    void testSaveLambdaNoMatch() {
        //given
        final String MATCH_ME = "MATCH_ME";
        Speciality speciality = new Speciality();
        speciality.setDescription("Not a match");

        Speciality savedSpecialty = new Speciality();
        savedSpecialty.setId(1l);

        given(specialtyRepository.save(argThat(argument
                -> argument.getDescription().equals(MATCH_ME)))).willReturn(savedSpecialty);

        //when
        Speciality returnedSpecialty = service.save(speciality);

        //then
        assertNull(returnedSpecialty);
    }

}