package lab4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private EmailProvider emailProvider;

    private LibraryService libraryService;

    private final UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final String email = "test@email.com";

    @BeforeEach
    void setUp() {
        libraryService = new LibraryService(emailProvider, resourceRepository);
    }

    @Test
    @DisplayName("T1: null resourceId returns false with no interactions")
    void T1() throws DatabaseFailureException, EmailFailureException {
        boolean result = libraryService.checkoutResource(null, email);

        assertFalse(result);
        verifyNoInteractions(resourceRepository, emailProvider);
    }

    @Test
    @DisplayName("T2: unavailable resource returns false")
    void T2() throws DatabaseFailureException, EmailFailureException {
        when(resourceRepository.isResourceAvailable(id)).thenReturn(false);

        boolean result = libraryService.checkoutResource(id, email);

        assertFalse(result);
        verify(resourceRepository, never()).updateStatus(any(), anyBoolean());
        verifyNoInteractions(emailProvider);
    }

    @Test
    @DisplayName("T3: updateStatus false throws DatabaseFailureException")
    void T3() throws DatabaseFailureException {
        when(resourceRepository.isResourceAvailable(id)).thenReturn(true);
        when(resourceRepository.updateStatus(id, false)).thenReturn(false);

        assertThrows(DatabaseFailureException.class, () -> libraryService.checkoutResource(id, email));
        verifyNoInteractions(emailProvider);
    }

    @Test
    @DisplayName("T4: updateStatus exception propagates DatabaseFailureException")
    void T4() throws DatabaseFailureException {
        when(resourceRepository.isResourceAvailable(id)).thenReturn(true);
        when(resourceRepository.updateStatus(id, false)).thenThrow(new DatabaseFailureException("Could not check out item."));

        assertThrows(DatabaseFailureException.class, () -> libraryService.checkoutResource(id, email));
        verifyNoInteractions(emailProvider);
    }

    @Test
    @DisplayName("T5: sendEmail false throws EmailFailureException")
    void T5() throws DatabaseFailureException, EmailFailureException {
        when(resourceRepository.isResourceAvailable(id)).thenReturn(true);
        when(resourceRepository.updateStatus(id, false)).thenReturn(true);
        when(emailProvider.sendEmail(any(), any())).thenReturn(false);

        assertThrows(EmailFailureException.class, () -> libraryService.checkoutResource(id, email));
        verify(resourceRepository, times(1)).updateStatus(id, false);
    }

    @Test
    @DisplayName("T6: sendEmail exception propagates EmailFailureException")
    void T6() throws DatabaseFailureException, EmailFailureException {
        when(resourceRepository.isResourceAvailable(id)).thenReturn(true);
        when(resourceRepository.updateStatus(id, false)).thenReturn(true);
        when(emailProvider.sendEmail(any(), any())).thenThrow(new EmailFailureException("Could not send email."));

        assertThrows(EmailFailureException.class, () -> libraryService.checkoutResource(id, email));
        verify(resourceRepository, times(1)).updateStatus(id, false);
    }

    @Test
    @DisplayName("T7: successful checkout returns true and sends email")
    void T7() throws DatabaseFailureException, EmailFailureException {
        when(resourceRepository.isResourceAvailable(id)).thenReturn(true);
        when(resourceRepository.updateStatus(id, false)).thenReturn(true);
        when(emailProvider.sendEmail(email, "Resource ID: " + id + " checked out.")).thenReturn(true);

        boolean result = libraryService.checkoutResource(id, email);

        assertTrue(result);
        verify(resourceRepository, times(1)).updateStatus(id, false);
        verify(emailProvider, times(1)).sendEmail(email, "Resource ID: " + id + " checked out.");
    }
}
