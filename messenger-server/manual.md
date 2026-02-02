main/ServerMain

해야 할 것
•	포트 결정(상수 or args)
•	ServerSocket 생성
•	AcceptLoop 생성(서버소켓, dispatcher, threadFactory 같은 의존성 주입)
•	ThreadFactory로 accept 스레드 시작

완료 기준
•	실행하면 서버가 포트 리스닝 상태가 됨.

⸻

runnable/AccepLoop

해야 할 것
•	run()에서 무한 accept
•	accept 된 Socket으로 ClientChannel 생성
•	ClientWorker 생성해서 ThreadFactory.newClientThread(worker).start()

완료 기준
•	클라이언트 연결이 오면 worker가 실행됨(로그 찍히면 좋음)

⸻

runnable/ClientWorker

해야 할 것
•	run()에서 메시지 읽기/디스패치/응답쓰기 루프

반드시 지켜야 하는 것
•	InputStream read는 common의 PacketUtils.readMessage() 사용
•	OutputStream write는 ClientChannel.send(Message) 사용
•	예외 나면 소켓/채널 close + 종료

완료 기준
•	클라가 LOGIN 보내면 서버가 응답을 보내고, 클라가 그걸 받는다.

⸻

thread/channel/ClientChennel

(파일명이 Chennel 오타지만 유지해도 됨)
해야 할 것
•	Socket 보관
•	send(Message msg) 구현: PacketUtils.formatMessage(msg) 결과를 OutputStream에 write/flush
•	InputStream getInputStream() 또는 worker가 socket에서 직접 가져가도 됨(둘 중 하나로 통일)
•	close()

완료 기준
•	worker가 channel 통해 응답을 안전하게 보낼 수 있음(동시성 대비 synchronized 해도 됨)

⸻

thread/pool/ThreadFactory

해야 할 것
•	Thread newAcceptThread(Runnable r)
•	Thread newClientThread(Runnable r)
•	스레드 이름 지정(accept-1, client-1 …)
•	daemon 여부 결정(보통 false)

완료 기준
•	서버가 스레드 생성 정책을 여기서만 바꿈(확장성)

⸻

method/parser/Disatcher (오타 Dispatcher)

해야 할 것
•	dispatch(Message req, ClientChennel channel) 메서드
•	req.header.type 문자열을 기준으로 handler 선택
•	handler 없으면 ERROR 응답

권장 구조
•	생성자에서 Map<String, Handler> 등록
•	handlers.put(LoginType.LOGIN.wire(), new LoginHandler(...)) 같은 방식

완료 기준
•	type 추가할 때 dispatcher 수정이 최소화(등록만 추가)

⸻

method/parser/handler/Handler

해야 할 것
•	인터페이스 시그니처 고정
•	예: Message handle(Message request, ClientChennel channel)

여기서 중요한 규칙
•	Handler는 “Message 받고 Message 반환”
•	소켓 IO는 하지 않고, 비즈니스는 AuthService 같은 service로 위임

⸻

method/parser/handler/impl/LoginHandler

해야 할 것
•	request.data에서 userId/password 꺼내기(너 common Message 구조에 맞게)
•	AuthService.login(userId, password, channel) 호출
•	성공: Responses.ok(LoginType.LOGIN_SUCCESS..., data)
•	실패: Responses.error(ErrorCodes.AUTH_INVALID_CREDENTIALS, "...")

완료 기준
•	로그인 성공하면 sessionId를 응답 data에 넣어서 내려준다.

⸻

method/parser/handler/impl/LogoutHandler

해야 할 것
•	request.header.sessionId 꺼내기
•	AuthService.logout(sessionId) 호출
•	성공: Responses.ok(LogoutType.LOGOUT_SUCCESS..., data)
•	실패: Responses.error(ErrorCodes.AUTH_INVALID_SESSION, "...")

⸻

service/AuthService

해야 할 것
•	login(userId, password, channel):
•	유저 검증(현재는 간단하게 하드코딩 or repo 추가)
•	UUID session 발급
•	SessionRepository에 저장
•	sessionId 반환
•	logout(sessionId):
•	SessionRepository에 session 존재 확인
•	삭제

확장성 관점에서 추천
•	유저 검증은 지금은 하드코딩으로 시작하고, 나중에 UserRepository 추가해도 AuthService 시그니처는 유지되게 설계

⸻

repo/InMemorySessionRepository

해야 할 것
•	sessionId → userId 저장/조회/삭제
•	Optional<String> findUserId(String sessionId)
•	void save(String sessionId, String userId)
•	void delete(String sessionId)

완료 기준
•	로그아웃/세션검증에서 안정적으로 동작

⸻

method/response/Responses

해야 할 것
•	ok(String type, Object data) → Message 생성
•	header.success=true
•	header.timestamp=now
•	header.type=성공타입
•	header.sessionId는 상황에 따라 넣거나(로그인 성공 응답은 넣어도 되고 data에만 넣어도 됨) 규칙 통일
•	error(String code, String message) → Message 생성
•	header.type=“ERROR”
•	success=false
•	data = new ErrorBody(code,message)

완료 기준
•	서버의 모든 응답 형태가 통일됨

⸻

method/response/ErrorCodes

해야 할 것
•	에러 코드 상수화
•	AUTH.INVALID_CREDENTIALS
•	AUTH.INVALID_SESSION
•	REQUEST.UNKNOWN_TYPE
•	(추후) ROOM.NOT_FOUND, USER.NOT_FOUND…

완료 기준
•	문자열 오타로 디버깅하는 일 방지