package com.spring.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.jboss.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.spring.dto.DocumentDto;
import com.spring.model.Document;
import com.spring.model.DocumentType;
import com.spring.model.Project;
import com.spring.model.Sprint;
import com.spring.model.Team;
import com.spring.model.User;
import com.spring.model.UserAccount;
import com.spring.repository.DocumentRepository;
import com.spring.security.UserAccountService;

@Service
@Transactional
public class DocumentService extends AbstractService {
	@Autowired
	private DocumentRepository documentRepo;
	@Autowired
	private SprintService sprintService;
	@Autowired
	private UserRolService userRolService;
	@Autowired
	private UserService userService;

	public static final String DATE_FORMAT = "dd/MM/yyyy";

	protected final Logger log = Logger.getLogger(DocumentService.class);

	public Document findOne(int id) {
		return this.documentRepo.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "The requested document not exists"));
	}

	public DocumentDto findOneDto(int id) {
		Document doc = this.findOne(id);
		checkUserOnTeam(UserAccountService.getPrincipal(), doc.getSprint().getProject().getTeam());
		return new DocumentDto(doc.getId(), doc.getName(), String.valueOf(doc.getType()), doc.getContent(),
				doc.getSprint().getId());
	}

	public List<DocumentDto> findAllBySprint(int sprintId) {
		Sprint sprint = this.sprintService.getOne(sprintId);
		checkUserOnTeam(UserAccountService.getPrincipal(), sprint.getProject().getTeam());
		List<Document> docs = this.documentRepo.findBySprint(sprint);
		return docs.stream().map(x -> new DocumentDto(x.getId(), x.getName(), String.valueOf(x.getType()),
				x.getContent(), sprint.getId())).collect(Collectors.toList());
	}

	public void saveDaily(String name, Sprint sprint) {
		this.documentRepo.saveAndFlush(new Document(DocumentType.DAILY, name, "[]", sprint, false));
	}

	public DocumentDto save(DocumentDto document, int sprintId) {
		checkType(document.getType());
		Sprint sprint = this.sprintService.getOne(sprintId);
		checkUserOnTeam(UserAccountService.getPrincipal(), sprint.getProject().getTeam());
		Document entity = new Document(DocumentType.valueOf(document.getType()), document.getName(),
				document.getContent(), sprint, false);
		Document saved = this.documentRepo.saveAndFlush(entity);
		return new DocumentDto(saved.getId(), saved.getName(), saved.getType().toString(), saved.getContent(),
				saved.getSprint().getId());
	}

	public DocumentDto update(DocumentDto dto, int documentId) {
		checkType(dto.getType());
		Document db = this.findOne(documentId);
		checkUserOnTeam(UserAccountService.getPrincipal(), db.getSprint().getProject().getTeam());
		db.setContent(dto.getContent());
		db.setType(DocumentType.valueOf(dto.getType()));
		db.setName(dto.getName());
		db = this.documentRepo.saveAndFlush(db);
		return new DocumentDto(db.getId(), db.getName(), db.getType().toString(), db.getContent(),
				db.getSprint().getId());
	}

	public Integer getDaily(int idSprint) {
		Integer res;
		Sprint sprint = this.sprintService.getOne(idSprint);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 2);
		Date actualDate = cal.getTime();
		String date = new SimpleDateFormat(DATE_FORMAT).format(actualDate);
		String name = "Daily " + date;
		List<Integer> dailys = this.documentRepo.getDaily(sprint, name);
		if (!dailys.isEmpty()) {
			res = dailys.get(0);
		} else {
			res = -1;
		}
		return res;
	}

	public void delete(int idDoc) {
		checkEntityExists(idDoc);
		Document doc = this.findOne(idDoc);
		checkUserOnTeam(UserAccountService.getPrincipal(), doc.getSprint().getProject().getTeam());
		this.documentRepo.delete(doc);
	}

	private void checkUserOnTeam(UserAccount user, Team team) {
		User usuario = this.userService.getUserByPrincipal();
		if (!this.userRolService.isUserOnTeam(usuario, team))
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,
					"The user " + user.getUsername() + " does not belong to the team: " + team.getName());
	}

	private void checkType(String check) {
		if (check == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Type cannot be null");
		} else {
			try {
				DocumentType.valueOf(check);
			} catch (IllegalArgumentException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Type does not match any: " + Arrays.asList(DocumentType.values()).stream()
								.map(DocumentType::toString).collect(Collectors.joining(",")));
			}
		}
	}

	private void checkEntityExists(Integer check) {
		if (!this.documentRepo.existsById(check)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The requested document is not avaiable");
		}
	}

	public ByteArrayInputStream generatePdf(int doc) {
		DocumentDto dto = this.findOneDto(doc);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// 1. Configuración de cabeceras

		// Necesario para no confundir el objeto documento de las entidades con el
		// objeto documento de la libreria de PDF
		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
		try {

			String type = dto.getType();
			String title = dto.getName();
			String content = dto.getContent();

			Sprint sprint = sprintService.getOne(dto.getSprint());

			Date start = sprint.getStartDate();
			Date end = sprint.getEndDate();

			Project project = sprint.getProject();
			Team team = project.getTeam();

			PdfWriter.getInstance(document, out);
			document.open();

			document.setPageSize(PageSize.A4);

			BaseFont helvetica = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);

			Font fontTitle = new Font(helvetica, 30, Font.BOLD);
			Font fontTitle2 = new Font(helvetica, 14, Font.BOLD);

			Font fontNormal = new Font(helvetica, 14, Font.NORMAL);
			Font fontCursiva = new Font(helvetica, 14, Font.ITALIC);
			Font fontSubtitle = new Font(helvetica, 14, Font.BOLDITALIC);

			Image img = Image.getInstance("files/logo.png");
			img.scalePercent(10);
			img.setAlignment(Element.ALIGN_RIGHT);

			// Cabecera

			PdfPTable table = new PdfPTable(2);

			table.setWidthPercentage(100);

			SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
			
			PdfPCell left = getCell(
					"Sprint: " + format.format(start) + " - " + format.format(end) + "\n" + "Proyecto: "
							+ project.getName() + "\nEquipo: " + team.getName() + "\nFecha de descarga: "
							+ LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
							Element.ALIGN_LEFT, fontCursiva);
			table.addCell(left);
			PdfPCell right = getCell("Text on the right", Element.ALIGN_RIGHT, fontNormal);
			right.addElement(img);

			table.addCell(right);
			document.add(table);

			// Contenido

			document.add(Chunk.NEWLINE);

			Paragraph p = new Paragraph(title, fontTitle);
			document.add(p);
			Paragraph p2 = new Paragraph(type.replace("_", " "), fontSubtitle);
			document.add(p2);

			document.add(Chunk.NEWLINE);

			// Contenido para cada campo

			generateFieldsByType(document, DocumentType.valueOf(type), content, fontTitle2, fontNormal);
			

		} catch (DocumentException e) {
			log.error("DocumentException", e);
		} catch (MalformedURLException e) {
			log.error("MalformedURLException", e);
		} catch (IOException e) {
			log.error("IOException", e);
		}

		document.close();
		return new ByteArrayInputStream(out.toByteArray());

	}

	private void generateFieldsByType(com.itextpdf.text.Document document, DocumentType type, String content,
			Font fontTitle, Font fontNormal) {

		JSONParser parser = new JSONParser();
		JSONObject object = null;
		JSONArray array = null;

		Map<String, String> values = new LinkedHashMap<>();

		try {
			if (type.equals(DocumentType.DAILY)) {
				array = (JSONArray) parser.parse(content);
				String todo = "";
				String done = "";
				String problems = "";
				for (int i = 0; i < array.size(); i++) {
					JSONObject o = (JSONObject) array.get(i);
					
					StringBuilder doneBl = new StringBuilder();
					doneBl.append(done + (String) o.get("name") + ":\n" + (String) o.get("done") + "\n\n");
					done = doneBl.toString();
					
					StringBuilder todoBl = new StringBuilder();
					todoBl.append(todo + (String) o.get("name") + ":\n" + (String) o.get("doing") + "\n\n");
					todo = todoBl.toString();
					
					StringBuilder problemsBl = new StringBuilder();
					problemsBl.append(problems + (String) o.get("name") + ":\n" + (String) o.get("problems") + "\n\n");
					problems = problemsBl.toString();
				}
				values.put("¿Qué he hecho hoy?", done);
				values.put("¿Qué voy a hacer hoy?", todo);
				values.put("¿Qué problemas he tenido?", problems);
				crearBody(document, values, fontTitle, fontNormal);
			} else if (type.equals(DocumentType.PLANNING_MEETING)) {
				object = (JSONObject) parser.parse(content);
				String entrega = (String) object.get("entrega");
				String conseguir = (String) object.get("conseguir");
				values.put("¿Qué puede entregarse en el Incremento resultante del Sprint que comienza?", entrega);
				values.put("¿Cómo se conseguirá hacer el trabajo necesario para entregar el Incremento?", conseguir);
				crearBody(document, values, fontTitle, fontNormal);
			} else if (type.equals(DocumentType.MIDDLE_REVIEW) || type.equals(DocumentType.REVIEW)) {
				object = (JSONObject) parser.parse(content);
				String done = (String) object.get("done");
				String noDone = (String) object.get("noDone");
				String rePlanning = (String) object.get("rePlanning");
				values.put("Qué elementos del product backlog se han \"Terminado\"", done);
				values.put("Cuáles no se han \"Terminado\"", noDone);
				values.put("Planificación futura", rePlanning);
				crearBody(document, values, fontTitle, fontNormal);
			} else if (type.equals(DocumentType.MIDDLE_RETROSPECTIVE) || type.equals(DocumentType.RETROSPECTIVE)) {
				object = (JSONObject) parser.parse(content);
				String good = (String) object.get("good");
				String bad = (String) object.get("bad");
				String improvement = (String) object.get("improvement");
				values.put("¿Qué se ha hecho bien?", good);
				values.put("¿Qué se ha hecho mal?", bad);
				values.put("¿Como podemos mejorar?", improvement);
				crearBody(document, values, fontTitle, fontNormal);
			}

		} catch (ParseException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parsing content action has not been possible");
		} catch (NullPointerException e) {
			log.error("NullPointerException", e);
		}
	}

	public void crearBody(com.itextpdf.text.Document document, Map<String, String> values, Font fontTitle,
			Font fontNormal) {

		values.keySet().forEach(titulo -> {
			try {
				String contenido = values.get(titulo);
				PdfPTable contentTable1 = new PdfPTable(1);
				contentTable1.setWidthPercentage(100);
				PdfPCell fieldTable = getCell(titulo, Element.ALIGN_LEFT, fontTitle);
				contentTable1.addCell(fieldTable);
				document.add(contentTable1);

				document.add(Chunk.NEWLINE);

				PdfPTable contentTable2 = new PdfPTable(1);
				contentTable2.setWidthPercentage(100);
				PdfPCell fieldTable2 = getCell(contenido, Element.ALIGN_LEFT, fontNormal);
				contentTable2.addCell(fieldTable2);

				document.add(contentTable2);
				document.add(Chunk.NEWLINE);
			} catch (DocumentException e) {
				log.error("DocumentException", e);
			}
		});

	}

	public void flush() {
		this.documentRepo.flush();
	}

	public Boolean checkDocumentIsCreated(String title, Sprint sprint) {

		title = title.toLowerCase();
		DocumentType type = null;
		if (title.contains("middle") && title.contains("review")) {
			type = DocumentType.MIDDLE_REVIEW;
		} else if (title.contains("middle") && title.contains("retrospective")) {
			type = DocumentType.MIDDLE_RETROSPECTIVE;
		} else if (title.contains("review")) {
			type = DocumentType.REVIEW;
		} else if (title.contains("retrospective")) {
			type = DocumentType.RETROSPECTIVE;
		}
		List<Document> documents = this.documentRepo.findBySprintAndTypeAndNotified(sprint, type, false);
		if (!documents.isEmpty()) {
			Document document = documents.get(0);
			document.setNotified(true);
			this.documentRepo.saveAndFlush(document);
		}
		return !documents.isEmpty();
	}

	private PdfPCell getCell(String text, int aligment, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setPadding(0);
		cell.setHorizontalAlignment(aligment);
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}

}