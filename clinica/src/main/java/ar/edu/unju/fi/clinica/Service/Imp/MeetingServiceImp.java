/**
 * 
 */
package ar.edu.unju.fi.clinica.Service.Imp;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.unju.fi.clinica.DTO.MeetingDTO;
import ar.edu.unju.fi.clinica.DTO.PatientDTO;
import ar.edu.unju.fi.clinica.Entity.Meeting;
import ar.edu.unju.fi.clinica.Mapper.Mapper;
import ar.edu.unju.fi.clinica.Repository.MeetingRepository;
import ar.edu.unju.fi.clinica.Repository.PatientRepository;
import ar.edu.unju.fi.clinica.Service.MailService;
import ar.edu.unju.fi.clinica.Service.MeetingService;
import ar.edu.unju.fi.clinica.Util.FormatLocalDate;

/**
 * @author Diaz, Sebastián Darío
 * Clase de implementación del servicio MeetingService.
 */
@Service
public class MeetingServiceImp implements MeetingService {
	private static Logger log = Logger.getLogger(MeetingServiceImp.class);
	
	@Autowired
	private MeetingRepository meetingRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	private MailService mailService;
	@Override
	public void addMeeting(MeetingDTO meetingDTO) {
		try {
			meetingRepository.save(Mapper.mapToMeeting(meetingDTO));
			mailService.sendMail("emailtesterapi.2022@gmail.com", meetingDTO.getPatientMeeting().getEmail(), "Aviso de Turno",bodyEmail(meetingDTO));

		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());
		}
	}

	/**
	 * Método de configuración del cuerpo de mensaje para la notificación de un turno.
	 * @param meetingDTO - turno: MeetingDTO
	 * @return Un String que contiene el cuerpo del mensaje.
	 */
	private String bodyEmail(MeetingDTO meetingDTO) {
		return "Estimado Sr.: " + meetingDTO.getPatientMeeting().getFirstName()+ " " 
				+ meetingDTO.getPatientMeeting().getLastName() + ", su turno se ha registrado para el día "
				+ FormatLocalDate.formatterLocalDateToString(meetingDTO.getStartMeeting())+" desde las " 
				+ FormatLocalDate.formatterTimeToString(meetingDTO.getStartMeeting()) + "hs, hasta las " 
				+ FormatLocalDate.formatterTimeToString(meetingDTO.getEndMeeting()) +"hs. En el "
				+ meetingDTO.getDoctorMeeting().getOfficeDoctor().getFloor() +", consultorio N° "+ meetingDTO.getDoctorMeeting().getOfficeDoctor().getNumberOffice()
				+ ". Se ruega puntualidad." ;
	}
	
	private String bodyEmailM(Meeting meeting) {
		return "Estimado Sr.: " + meeting.getPatientMeeting().getFirstName()+ " " 
				+ meeting.getPatientMeeting().getLastName() + ", su turno se ha registrado para el día "
				+ FormatLocalDate.formatterLocalDateToString(meeting.getStartMeeting())+" desde las " 
				+ FormatLocalDate.formatterTimeToString(meeting.getStartMeeting()) + "hs, hasta las " 
				+ FormatLocalDate.formatterTimeToString(meeting.getEndMeeting()) +"hs. En el "
				+ meeting.getDoctorMeeting().getOffice().getFloor() +", consultorio N° "+ meeting.getDoctorMeeting().getOffice().getNumberOffice()
				+ ". Se ruega puntualidad." ;
	}
	@Override
	public void updateMeeting(MeetingDTO meetingDTOOriginal, MeetingDTO meetingDTOUpdated) {
		try {
			MeetingDTO meetAux = new MeetingDTO();
			meetAux = Mapper.mapToMeetingDTO(meetingRepository.findByIdMeeting(meetingDTOOriginal.getIdMeeting()));
			meetAux.setDoctorMeeting(meetingDTOUpdated.getDoctorMeeting());
			meetAux.setEndMeeting(meetingDTOUpdated.getEndMeeting());
			meetAux.setPatientMeeting(meetingDTOUpdated.getPatientMeeting());
			meetAux.setStartMeeting(meetingDTOUpdated.getStartMeeting());
			
			meetingRepository.save(Mapper.mapToMeeting(meetAux));
		} catch (Exception e) {
			log.debug(e.getStackTrace());
		}
	}

	@Override
	public void deleteMeeting(MeetingDTO meetingDTO) {
		try {
			meetingRepository.delete(Mapper.mapToMeeting(meetingDTO));
		} catch (Exception e) {
			log.debug(e.getStackTrace());
		}
	}

	@SuppressWarnings("finally")
	@Override
	public MeetingDTO findMeetingByPatient(PatientDTO patientDTO) {
		try {
			Mapper.mapToMeetingDTO(meetingRepository.findByPatientMeeting(Mapper.mapToPatient(patientDTO)));
		} catch (Exception e) {
			log.debug(e.getStackTrace());
		}finally {
			return Mapper.mapToMeetingDTO(meetingRepository.findByPatientMeeting(Mapper.mapToPatient(patientDTO)));
		}
	}

	@Override
	public List<Meeting> listMeeting() {
		return meetingRepository.findAll();
	}

	@Override
	public MeetingDTO findMeetingById(Long idMeeting) {
		return Mapper.mapToMeetingDTO(meetingRepository.findByIdMeeting(idMeeting));
	}

	@Override
	public void deleteMeetingById(Long idMeeting) {
		try {
			meetingRepository.deleteById(idMeeting);
		} catch (Exception e) {
			log.debug(e.getStackTrace());
		}
	}

	@Override
	public List<Meeting> listMeetingAsc() {
		return meetingRepository.findAll(Sort.by("startMeeting").ascending());
	}

	@Override
	public MeetingDTO addMeetingDTO(MeetingDTO meetingDTO) {
		MeetingDTO meeDto = null;
		try {
			meetingRepository.save(Mapper.mapToMeeting(meetingDTO));
			mailService.sendMail("emailtesterapi.2022@gmail.com", meetingDTO.getPatientMeeting().getEmail(), "Aviso de Turno",bodyEmail(meetingDTO));
			meeDto = meetingDTO;
		} catch (Exception e) {
			log.debug(e.getCause());
		}
		return meeDto;
	}

	@Override
	public void addMeetingEntity(Meeting meeting) {
		try {
			meetingRepository.save(meeting);
			mailService.sendMail("emailtesterapi.2022@gmail.com", meeting.getPatientMeeting().getEmail(), "Aviso de Turno",bodyEmailM(meeting));

		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());
		}
	}

	@Override
	public void updateMeetingNormal(Meeting meetingOriginal, Meeting meetingUpdated) {
		try {
			Meeting meetAux = new Meeting();
			meetAux = meetingRepository.findByIdMeeting(meetingOriginal.getIdMeeting());
			meetAux.setDoctorMeeting(meetingUpdated.getDoctorMeeting());
			meetAux.setEndMeeting(meetingUpdated.getEndMeeting());
			meetAux.setPatientMeeting(meetingUpdated.getPatientMeeting());
			meetAux.setStartMeeting(meetingUpdated.getStartMeeting());
			meetAux.setStatus(true);
			meetingRepository.save(meetAux);
		} catch (Exception e) {
			log.debug(e.getLocalizedMessage());
		}
		
	}
}
