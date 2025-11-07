import json

from django.contrib.auth import authenticate, login, get_user_model
from django.shortcuts import render
from django.http import JsonResponse
from django.contrib.auth.hashers import make_password
from django.views.decorators.csrf import csrf_exempt

from users.models import Organization

@csrf_exempt
def register(request):
    if request.method == 'POST':
        try:
            # Giải mã dữ liệu JSON từ request.body
            data = json.loads(request.body)

            # Truy cập các trường từ dữ liệu JSON
            email = data.get('email')
            password = data.get('password')

            # Kiểm tra các trường bắt buộc
            if not email or not password:
                return JsonResponse({"message": "Email and password are required"}, status=400)

            # Kiểm tra hoặc tạo mới tổ chức (organization)
            organization_name = data.get('organization', 'Default Org')  # Nếu không có, sử dụng mặc định
            organization, created = Organization.objects.get_or_create(name=organization_name)

            # Lấy model User từ AUTH_USER_MODEL
            User = get_user_model()

            # Tạo người dùng mới
            user = User.objects.create_user(
                email=email,
                password=password,
                organization=organization
            )

            return JsonResponse({"message": "User registered successfully"})

        except json.JSONDecodeError:
            return JsonResponse({"message": "Invalid JSON format"}, status=400)
    return JsonResponse({"message": "Invalid request method"}, status=400)

@csrf_exempt

def user_login(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        email = data['email']
        password = data['password']

        # Authenticate user
        user = authenticate(request, email=email, password=password)

        if user is not None:
            login(request, user)
            return JsonResponse({"message": "Login successful"})
        else:
            return JsonResponse({"message": "Invalid credentials"}, status=400)
    return render(request)

@csrf_exempt
def create_organization(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        org_name = data['name']
        organization = Organization.objects.create(name=org_name)
        return JsonResponse({"message": "Organization created successfully"})
    return render(request, 'create_organization.html')